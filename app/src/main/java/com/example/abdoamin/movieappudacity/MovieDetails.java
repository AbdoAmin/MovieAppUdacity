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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdoamin.movieappudacity.apiRequire.ApiClient;
import com.example.abdoamin.movieappudacity.apiRequire.ApiInterface;
import com.example.abdoamin.movieappudacity.data.MovieContract;
import com.example.abdoamin.movieappudacity.myObject.Movie;
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

    private static final int DELETE_LOADER_ID = 2;
    private static final int ADD_LOADER_ID = 1;
    private static final int IS_FAVORITE_LOADER_ID =0 ;
    Context mContext;
    Movie mMovie;
    private boolean favorite = false;
    private ImageView moviePoster;
    private ImageView favoriteIcon;
    private TextView movieName;
    private TextView movieRate;
    private TextView movieDate;
    private TextView movieDescription;
    private ListView trailersLink;
    private ArrayList<String> links = new ArrayList<String>();
    static int position;
    ApiInterface api_service =
            ApiClient.getClient().create(ApiInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        mContext=this;
        mMovie = getIntent().getParcelableExtra("theMovie");
        favoriteIcon = (ImageView) findViewById(R.id.favorite_icon);
        moviePoster = (ImageView) findViewById(R.id.movie_poster_details);
        movieName = (TextView) findViewById(R.id.movie_title);
        movieRate = (TextView) findViewById(R.id.movie_rate);
        movieDate = (TextView) findViewById(R.id.movie_date);
        movieDescription = (TextView) findViewById(R.id.movie_overview);
        trailersLink = (ListView) findViewById(R.id.trail_list);

        Picasso.with(this).load(Uri.parse("https://image.tmdb.org/t/p/w185" + mMovie.getPosterPath())).into(moviePoster);
        movieName.setText(mMovie.getOriginal_title());
        movieDate.setText(mMovie.getReleaseDate());
        movieDescription.setText(mMovie.getOverview());
        movieRate.setText(mMovie.getVoteAverage().toString()+"/10");
        getLoaderManager().initLoader(IS_FAVORITE_LOADER_ID, null,isFavorite);


        favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!favorite) {
                    getLoaderManager().initLoader(ADD_LOADER_ID, null, addFavoriteMovie);

                } else {
                    getLoaderManager().initLoader(DELETE_LOADER_ID, null,deleteFavoriteMovie);
                }

            }
        });

        TrailerTask task = new TrailerTask();
        task.execute();

    }
    public class TrailerTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);

            Call<TrailersResponse> call = apiService.getMovieTrailers(mMovie.getId(), API_KEY);
            call.enqueue(new Callback<TrailersResponse>() {
                @Override
                public void onResponse(Call<TrailersResponse> call, Response<TrailersResponse> response) {
                    List<Trailers> trailers = response.body().getResults();
                    mMovie.setTrailers(trailers);
                    for (Trailers link : mMovie.getTrailers()) {
                        if (link.getSite().equals("YouTube"))
                            links.add(/*"https://www.youtube.com/watch?v=" + */link.getKey());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, links);
                    trailersLink.setAdapter(adapter);
                    trailersLink.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent appYoutube = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + links.get(position)));
                            Intent webYoutube = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://www.youtube.com/watch?v=" + links.get(position)));
                            try {
                                startActivity(appYoutube);
                            } catch (ActivityNotFoundException ex) {
                                startActivity(webYoutube);
                            }

                        }
                    });

                }

                @Override
                public void onFailure(Call<TrailersResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("fff", t.toString());
                }
            });
            return null;
        }
    }

    private LoaderManager.LoaderCallbacks<Cursor> isFavorite = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {
            return new AsyncTaskLoader<Cursor>(getApplicationContext()) {

                Cursor mFavoriteMovies = null;
                @Override
                protected void onStartLoading() {
                    if (mFavoriteMovies != null) {
                        // Delivers any previously loaded data immediately
                        deliverResult(mFavoriteMovies);
                    } else {
                        // Force a new load
                        forceLoad();
                    }
                }
                @Override
                public Cursor loadInBackground() {
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
                public void deliverResult(Cursor data) {
                    mFavoriteMovies = data;
                    super.deliverResult(data);
                }

            };

        }


        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if (data.moveToFirst()) {
                favorite = true;
                favoriteIcon.setImageResource(R.drawable.ic_favorite_black_24dp);
            }


        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };


    private LoaderManager.LoaderCallbacks<Uri> addFavoriteMovie = new LoaderManager.LoaderCallbacks<Uri>() {
        @Override
        public Loader<Uri> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<Uri>(mContext) {
                @Override
                protected void onStartLoading() {
                        // Force a new load
                        forceLoad();

                }
                @Override
                public Uri loadInBackground() {

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
            };
        }

        @Override
        public void onLoadFinished(Loader<Uri> loader, Uri data) {
            if(data!=null) {
                Toast.makeText(mContext, data.toString(), Toast.LENGTH_SHORT).show();
                favoriteIcon.setImageResource(R.drawable.ic_favorite_black_24dp);
                favorite = !favorite;
            }

        }

        @Override
        public void onLoaderReset(Loader<Uri> loader) {

        }
    };


    private LoaderManager.LoaderCallbacks<Integer> deleteFavoriteMovie = new LoaderManager.LoaderCallbacks<Integer>() {
        @Override
        public Loader<Integer> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<Integer>(mContext) {
                @Override
                protected void onStartLoading() {
                        forceLoad();

                }
                @Override
                public Integer loadInBackground() {

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
                };
        }

        @Override
        public void onLoadFinished(Loader<Integer> loader, Integer data) {
            if (data != 0) {
                favoriteIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                favorite = !favorite;
                Toast.makeText(mContext, "Delete: " + data.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onLoaderReset(Loader<Integer> loader) {

        }
    } ;


}
