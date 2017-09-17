package com.example.abdoamin.movieappudacity.apiRequire;

/**
 * Created by Abdo Amin on 9/15/2017.
 */
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {
    public static final String TOP_RATED = "Top Rated";
    public static final String POPULAR = "Most Popular";
    public static final String MY_FAVORITE = "My Fatorite";
    public static final String API_KEY = "PAst your here";
    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}