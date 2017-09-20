package com.example.abdoamin.movieappudacity.myObject;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Abdo Amin on 9/18/2017.
 */

public class Review {
    @SerializedName("author")
    private String author;
    @SerializedName("content")
    private String content;

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
