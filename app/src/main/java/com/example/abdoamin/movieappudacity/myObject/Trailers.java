package com.example.abdoamin.movieappudacity.myObject;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Abdo Amin on 9/15/2017.
 */

public class Trailers {
    @SerializedName("name")
    private String name;
    @SerializedName("key")
    private String key;
    @SerializedName("site")
    private String site;


    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }


    public String getSite() {
        return site;
    }

}
