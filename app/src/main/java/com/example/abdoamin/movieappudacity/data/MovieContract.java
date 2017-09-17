package com.example.abdoamin.movieappudacity.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Abdo Amin on 9/15/2017.
 */

public class MovieContract {

    public static final String TABLE_CONTACTS = "favorite";

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.example.abdoamin.movieappudacity";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "favorite" directory
    public static final String PATH_FAVORITE = "favorite";

    /* FavoriteEntry is an inner class that defines the contents of the favorite table */
    public static final class FavoriteEntry implements BaseColumns {

        // FavoriteEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String TABLE_NAME = "favorite";



    }
}
