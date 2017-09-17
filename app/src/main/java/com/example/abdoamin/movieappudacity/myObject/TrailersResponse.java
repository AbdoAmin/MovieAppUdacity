package com.example.abdoamin.movieappudacity.myObject;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Abdo Amin on 9/15/2017.
 */

public class TrailersResponse {

    @SerializedName("id")
    private Integer id;
    @SerializedName("results")
    private List<Trailers> results;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Trailers> getResults() {
        return results;
    }

    public void setResults(List<Trailers> results) {
        this.results = results;
    }
}
