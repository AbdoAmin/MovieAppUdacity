package com.example.abdoamin.movieappudacity;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdoamin.movieappudacity.adapter.ReviewRecycleAdapter;
import com.example.abdoamin.movieappudacity.adapter.TrailersRecycleAdapter;
import com.example.abdoamin.movieappudacity.apiRequire.ApiClient;
import com.example.abdoamin.movieappudacity.apiRequire.ApiInterface;
import com.example.abdoamin.movieappudacity.data.MovieContract;
import com.example.abdoamin.movieappudacity.myObject.Movie;
import com.example.abdoamin.movieappudacity.myObject.Review;
import com.example.abdoamin.movieappudacity.myObject.ReviewResponse;
import com.example.abdoamin.movieappudacity.myObject.Trailers;
import com.example.abdoamin.movieappudacity.myObject.TrailersResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.DELETE;

import static com.example.abdoamin.movieappudacity.apiRequire.ApiClient.API_KEY;

public class MovieDetails extends AppCompatActivity {

    Context mContext;
    Movie mMovie;
    private boolean favorite = false;
    private ImageView moviePoster;
    private ImageView favoriteIcon;
    private TextView movieName;
    private TextView movieRate;
    private TextView movieDate;
    private TextView movieDescription;
    private RecyclerView mReviewRecyclerView,mTrailersRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        mContext=this;
        mMovie = getIntent().getParcelableExtra("theMovie");
        favoriteIcon = (ImageView) findViewById(R.id.favorite_icon);
        moviePoster = (ImageView) findViewById(R.id.movie_poster_details);
        movieName = (TextView) findViewById(R.id.movie_title);
        movieRate = (TextView) findViewById(R.id.movie_rate);
        movieDate = (TextView) findViewById(R.id.movie_date);
        movieDescription = (TextView) findViewById(R.id.movie_overview);
        mReviewRecyclerView= (RecyclerView) findViewById(R.id.review_RecyclerView);
        mTrailersRecyclerView= (RecyclerView) findViewById(R.id.trail_RecyclerView);

        Picasso.with(this).load(Uri.parse(getString(R.string.image_path) + mMovie.getPosterPath())).into(moviePoster);
        movieName.setText(mMovie.getOriginal_title());
        movieDate.setText(mMovie.getReleaseDate());
        movieDescription.setText(mMovie.getOverview());
        movieRate.setText(mMovie.getVoteAverage().toString()+"/10");
        IsFavoriteTask isFavorite=new IsFavoriteTask();
        isFavorite.execute();


        favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!favorite) {
                    AddFavoriteMovieTask addFavoriteMovie=new AddFavoriteMovieTask();
                    addFavoriteMovie.execute();

                } else {
                    DeleteFavoriteMovieTask deleteFavoriteMovie=new DeleteFavoriteMovieTask();
                    deleteFavoriteMovie.execute();
                }

            }
        });

        TrailerTask();
        ReviewTask();
    }


    public void TrailerTask () {
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);

            Call<TrailersResponse> call = apiService.getMovieTrailers(mMovie.getId(), API_KEY);
            call.enqueue(new Callback<TrailersResponse>() {
                @Override
                public void onResponse(Call<TrailersResponse> call, Response<TrailersResponse> response) {
                    List<Trailers> trailers = new ArrayList<Trailers>();
                    for (Trailers a :response.body().getResults())
                    {
                        if (a.getSite().equals("YouTube"))
                            trailers.add(a);
                    }
                    mMovie.setTrailers(trailers);
                    mTrailersRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                    mTrailersRecyclerView.setAdapter(new TrailersRecycleAdapter(trailers,R.layout.trailers_item,mContext));

                }

                @Override
                public void onFailure(Call<TrailersResponse> call, Throwable t) {
                    Log.e("<^_^>", t.toString());
                }
            });

    }

    public void ReviewTask (){


            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);

            Call<ReviewResponse> call = apiService.getMovieReviews(mMovie.getId(), API_KEY);
            call.enqueue(new Callback<ReviewResponse>() {
                @Override
                public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                    List<Review> reviews = response.body().getResults();
                    mMovie.setReviews(reviews);
                    mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                    mReviewRecyclerView.setAdapter(new ReviewRecycleAdapter(reviews,R.layout.review_item,mContext));
                }

                @Override
                public void onFailure(Call<ReviewResponse> call, Throwable t) {
                    Log.e("<^_^>", t.toString());
                }
            });

    }

    public class IsFavoriteTask extends AsyncTask<Void, Void, Cursor>{
        @Override
        protected Cursor doInBackground(Void... voids) {
            try {
                String stringId = Integer.toString(mMovie.getId());
                Uri uri = MovieContract.FavoriteEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();
                return getContentResolver().query(uri,
                        null,
                        null,
                        null,
                        null);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor.moveToFirst()) {
                favorite = true;
                favoriteIcon.setImageResource(R.drawable.ic_favorite_black_24dp);
            }
        }
    }


    public class AddFavoriteMovieTask extends AsyncTask<Void, Void, Uri>{
        @Override
        protected Uri doInBackground(Void... voids) {
            try {
                ContentValues value = new ContentValues();
                value.put(MovieContract.FavoriteEntry._ID, mMovie.getId());
                return getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI, value);
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Uri uri) {
            if(uri!=null) {
                Toast.makeText(mContext, uri.toString(), Toast.LENGTH_SHORT).show();
                favoriteIcon.setImageResource(R.drawable.ic_favorite_black_24dp);
                favorite = !favorite;
            }
        }
    }


    public class DeleteFavoriteMovieTask extends AsyncTask<Void, Void, Integer>{
        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                String stringId = Integer.toString(mMovie.getId());
                Uri uri = MovieContract.FavoriteEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();
                return getContentResolver().delete(uri,null,null);
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer != 0) {
                favoriteIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                favorite = !favorite;
                Toast.makeText(mContext, "Delete: " + integer.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }




}
