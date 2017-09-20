package com.example.abdoamin.movieappudacity.myObject;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Abdo Amin on 9/18/2017.
 */

public class ReviewResponse {
    @SerializedName("results")
    private List<Review> results;

    public List<Review> getResults() {
        return results;
    }
}
