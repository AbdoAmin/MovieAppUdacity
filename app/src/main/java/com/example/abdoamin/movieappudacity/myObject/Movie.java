package com.example.abdoamin.movieappudacity.myObject;

/**
 * Created by Abdo Amin on 9/15/2017.
 */


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Movie implements Parcelable{
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("overview")
    private String overview;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("id")
    private Integer id;
    @SerializedName("original_title")
    private String original_title;
    @SerializedName("vote_average")
    private Double voteAverage;
    //.....
    private List<Trailers> trailers;
    private List<Review> reviews;


    protected Movie(Parcel in) {
        id=in.readInt();
        posterPath = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        original_title = in.readString();
        voteAverage=in.readDouble();
    }


    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getPosterPath() {
        return posterPath;
    }


    public String getOverview() {
        return overview;
    }


    public String getReleaseDate() {
        return releaseDate;
    }


    public Integer getId() {
        return id;
    }


    public String getOriginal_title() {
        return original_title;
    }


    public Double getVoteAverage() {
        return voteAverage;
    }


    public List<Trailers> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailers> trailers) {
        this.trailers = trailers;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(posterPath);
        parcel.writeString(overview);
        parcel.writeString(releaseDate);
        parcel.writeString(original_title);
        parcel.writeDouble(voteAverage);

    }
}