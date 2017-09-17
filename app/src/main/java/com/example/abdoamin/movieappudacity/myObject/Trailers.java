package com.example.abdoamin.movieappudacity.myObject;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Abdo Amin on 9/15/2017.
 */

public class Trailers {
    @SerializedName("id")
    private String id;
    @SerializedName("key")
    private String key;
    @SerializedName("site")
    private String site;


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }


    public String getSite() {
        return site;
    }
    public void setSite(String site) {
        this.site = site;
    }

}
