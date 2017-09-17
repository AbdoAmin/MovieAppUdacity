package com.example.abdoamin.movieappudacity;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String CURRENT_MODE ="Last sorted mode" ;
    private static final int TASK_LOADER_ID = 0;
    private static final String TAG = "MainActivty";
    Context mContext;

    public RecyclerView mRecyclerView;
    public MyMovieRecycleAdabter mMyMovieRecycleAdabter;
    List<Movie> movies = new ArrayList<Movie>();
    List<FavoriteMovie> favoriteMovieList = new ArrayList<>();
    int mPageNum = 1;
    ApiInterface apiService =
            ApiClient.getClient().create(ApiInterface.class);

    private boolean loading = true;
    GridLayoutManager mGridLayoutManager;
    int pastVisiblesItems, visibleItemCount, totalItemCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
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
        getSupportActionBar().setTitle(mode);
        mGridLayoutManager = new GridLayoutManager(mContext,2);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        if (mode.equals(MY_FAVORITE)) {
            getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
        } else {
            Call<MoviesResponse> call = null;
            loading = true;

            if (mode.equals(TOP_RATED)) {
                call = apiService.getTopRatedMovies(API_KEY, mPageNum);
            } else if (mode.equals(POPULAR)) {
                call = apiService.getPopular(API_KEY, mPageNum);
            }
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    movies = response.body().getResults();
                    mRecyclerView.setAdapter(new MyMovieRecycleAdabter(movies, R.layout.movie_item, mContext));
                    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, final int dx, final int dy) {
                            if (dy > 0) //check for scroll down
                            {
                                visibleItemCount = mGridLayoutManager.getChildCount();
                                totalItemCount = mGridLayoutManager.getItemCount();
                                pastVisiblesItems = mGridLayoutManager.findFirstVisibleItemPosition();

                                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                    final int x=dx,y=dy;
                                    mPageNum++;
                                    Call<MoviesResponse> call = null;
                                    switch (mode) {
                                        case TOP_RATED:
                                            call = apiService.getTopRatedMovies(API_KEY, mPageNum);
                                            break;
                                        case POPULAR:
                                            call = apiService.getPopular(API_KEY, mPageNum);
                                            break;
                                    }

                                    call.enqueue(new Callback<MoviesResponse>() {
                                        @Override
                                        public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {

                                            movies.addAll(response.body().getResults());
                                            mMyMovieRecycleAdabter = new MyMovieRecycleAdabter(movies, R.layout.movie_item, mContext);
                                            mRecyclerView.setAdapter(mMyMovieRecycleAdabter);
                                            mRecyclerView.scrollTo(x,y);
                                        }

                                        @Override
                                        public void onFailure(Call<MoviesResponse> call, Throwable t) {
                                            // Log error here since request failed
                                            Log.e(TAG, t.toString());
                                        }
                                    });
                                }

                            }
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
    }


    //Loader part to get My Favorite Movie from db by contentProvider as new thread

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mFavoriteMoviesList = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                favoriteMovieList.clear();
                if (mFavoriteMoviesList != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mFavoriteMoviesList);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
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

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mFavoriteMoviesList = data;
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        if(data!=null) {
            data.moveToFirst();
            while (!data.isAfterLast()) {
                favoriteMovieList.add(new FavoriteMovie(data.getInt(0)));
                data.moveToNext();
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
                        if (favoriteMovieList.size() == movies.size()){
                            mRecyclerView.setAdapter(new MyMovieRecycleAdabter(movies, R.layout.movie_item, mContext));
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
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        favoriteMovieList.clear();
    }

    //End..


    //Here Prefrance Part to get last mode,Top rated mode as default

    private void setUpMyPrefrance(String mode){
        //key == SharedPreferences name // no refer to anything -_-
        SharedPreferences.Editor editor = getSharedPreferences(CURRENT_MODE, MODE_PRIVATE).edit();
        editor.putString(CURRENT_MODE, mode);
        editor.apply();
    }
    private void getMyPrefrance(){
        SharedPreferences prefs = getSharedPreferences(CURRENT_MODE, MODE_PRIVATE);
        getMoviesList(prefs.getString(CURRENT_MODE,TOP_RATED));
    }

    //End..
}
