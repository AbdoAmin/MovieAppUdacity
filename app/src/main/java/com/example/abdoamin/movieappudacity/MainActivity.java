package com.example.abdoamin.movieappudacity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.abdoamin.movieappudacity.adapter.MyMovieRecycleAdapter;
import com.example.abdoamin.movieappudacity.apiRequire.ApiClient;
import com.example.abdoamin.movieappudacity.apiRequire.ApiInterface;
import com.example.abdoamin.movieappudacity.data.MovieContract;
import com.example.abdoamin.movieappudacity.myObject.FavoriteMovie;
import com.example.abdoamin.movieappudacity.myObject.Movie;
import com.example.abdoamin.movieappudacity.myObject.MoviesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.abdoamin.movieappudacity.apiRequire.ApiClient.API_KEY;
import static com.example.abdoamin.movieappudacity.apiRequire.ApiClient.MY_FAVORITE;
import static com.example.abdoamin.movieappudacity.apiRequire.ApiClient.POPULAR;
import static com.example.abdoamin.movieappudacity.apiRequire.ApiClient.TOP_RATED;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivty";
    Context mContext;

    public RecyclerView mRecyclerView;
    public MyMovieRecycleAdapter mMyMovieRecycleAdapter;
    List<Movie> movies = new ArrayList<Movie>();
    List<FavoriteMovie> favoriteMovieList = new ArrayList<>();
    int mPageNum = 1;
    ApiInterface apiService =
            ApiClient.getClient().create(ApiInterface.class);
    GridLayoutManager mGridLayoutManager;
    private ProgressBar mProgressBar;
    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mTextView = (TextView) findViewById(R.id.textView);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mGridLayoutManager = new GridLayoutManager(mContext, 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        //get last mode lunched
        getMyPrefrance();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activty_menu, menu);
        return true;
    }

    //select item menu to select mode and save prefrance
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.top_rated_item) {
            getMoviesList(TOP_RATED);
            setUpMyPrefrance(TOP_RATED);
        } else if (item.getItemId() == R.id.popular_item) {
            getMoviesList(POPULAR);
            setUpMyPrefrance(POPULAR);
        } else if (item.getItemId() == R.id.favirte_item) {
            getMoviesList(MY_FAVORITE);
            setUpMyPrefrance(MY_FAVORITE);
        }
        return true;
    }

    //get Api and pass response to mRcycleview to represend movies as sorted by mode parameter
    private void getMoviesList(final String mode) {
        if (checkConnection()) {
            movies.clear();
            mTextView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle(mode);
            mRecyclerView.getRecycledViewPool().clear();

            if (mode.equals(MY_FAVORITE)) {
                GetFavoriteMovie getFavoriteMovie = new GetFavoriteMovie();
                getFavoriteMovie.execute();
            } else {
                Call<MoviesResponse> call = null;
                if (mode.equals(TOP_RATED)) {
                    call = apiService.getTopRatedMovies(API_KEY, mPageNum);
                } else if (mode.equals(POPULAR)) {
                    call = apiService.getPopular(API_KEY, mPageNum);
                }
                call.enqueue(new Callback<MoviesResponse>() {
                    @Override
                    public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                        mProgressBar.setVisibility(View.GONE);
                        movies = response.body().getResults();
                        mRecyclerView.setAdapter(new MyMovieRecycleAdapter(movies, R.layout.movie_item, mContext));
                        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mGridLayoutManager) {
                            @Override
                            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                                getMorePage(page,mode);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<MoviesResponse> call, Throwable t) {
                        // Log error here since request failed
                        Log.e(TAG, t.toString());
                    }
                });

            }
        } else {
            mProgressBar.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText("No Internet found");
        }
    }


    private void getMorePage(int page,String mode){
        Call<MoviesResponse> call = null;
        switch (mode) {
            case TOP_RATED:
                call = apiService.getTopRatedMovies(API_KEY, page);
                break;
            case POPULAR:
                call = apiService.getPopular(API_KEY, page);
                break;
        }

        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {

                movies.addAll(response.body().getResults());
                mMyMovieRecycleAdapter = new MyMovieRecycleAdapter(movies, R.layout.movie_item, mContext);
                mRecyclerView.setAdapter(mMyMovieRecycleAdapter);
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
    }


    //Loader part to get My Favorite Movie from db by contentProvider as new thread

    public class GetFavoriteMovie extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Void... voids) {
            try {
                return getContentResolver().query(MovieContract.FavoriteEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

            } catch (Exception e) {
                Log.e(TAG, "Failed to asynchronously load data.");
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            favoriteMovieList.clear();
            if (cursor.moveToFirst()) {

                while (!cursor.isAfterLast()) {
                    favoriteMovieList.add(new FavoriteMovie(cursor.getInt(0)));
                    cursor.moveToNext();
                }
                //
                movies.clear();
                Call<Movie> call = null;
                for (FavoriteMovie movie : favoriteMovieList) {
                    call = apiService.getMovieDetails(movie.getID(), API_KEY);
                    call.enqueue(new Callback<Movie>() {
                        @Override
                        public void onResponse(Call<Movie> call, Response<Movie> response) {
                            movies.add(response.body());
                            if (favoriteMovieList.size() == movies.size()) {
                                mProgressBar.setVisibility(View.GONE);
                                mRecyclerView.setAdapter(new MyMovieRecycleAdapter(movies, R.layout.movie_item, mContext));
                                mRecyclerView.clearOnScrollListeners();
                            }
                        }

                        @Override
                        public void onFailure(Call<Movie> call, Throwable t) {
                            // Log error here since request failed
                            Log.e(TAG, t.toString());
                        }
                    });
                }
            } else {
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText("you haven't Favorite Movie yet");
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }



    //Here Prefrance Part to get last mode,Top rated mode as default
    private void setUpMyPrefrance(String mode) {
        //key == SharedPreferences name // no refer to anything -_-
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.CURRENT_MODE), MODE_PRIVATE).edit();
        editor.putString(getString(R.string.CURRENT_MODE), mode);
        editor.apply();
    }

    private void getMyPrefrance() {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.CURRENT_MODE), MODE_PRIVATE);
        getMoviesList(prefs.getString(getString(R.string.CURRENT_MODE), TOP_RATED));
    }
    //End..


    //if no internet
    private boolean checkConnection() {
        ConnectivityManager mConnectivityManager =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        return mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting();
    }
}
