package com.example.abdoamin.movieappudacity.apiRequire;

/**
 * Created by Abdo Amin on 9/15/2017.
 */
import com.example.abdoamin.movieappudacity.myObject.Movie;
import com.example.abdoamin.movieappudacity.myObject.MoviesResponse;
import com.example.abdoamin.movieappudacity.myObject.TrailersResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiInterface {
    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey, @Query("page") Integer PageNum);

    @GET("movie/popular")
    Call<MoviesResponse> getPopular(@Query("api_key") String apiKey, @Query("page") Integer PageNum);

    @GET("movie/{id}")
    Call<Movie> getMovieDetails(@Path("id") Integer id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/videos")
    Call<TrailersResponse> getMovieTrailers(@Path("movie_id") Integer id, @Query("api_key") String apiKey);


}