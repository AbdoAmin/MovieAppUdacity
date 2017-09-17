package com.example.abdoamin.movieappudacity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.abdoamin.movieappudacity.myObject.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Abdo Amin on 9/15/2017.
 */

public class MyMovieRecycleAdabter extends RecyclerView.Adapter<MyMovieRecycleAdabter.ViewHolder> {

    private List<Movie> movies;
    private int movieLayout;
    private Context context;

    public MyMovieRecycleAdabter(List<Movie> movies, int movieLayout, Context context) {
        this.movies = movies;
        this.movieLayout = movieLayout;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(movieLayout,parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Picasso.with(context).load(Uri.parse("https://image.tmdb.org/t/p/w185"+movies.get(position).getPosterPath())).into( holder.moviePoster);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context,MovieDetails.class);
                intent.putExtra("theMovie",movies.get(position));

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
          public ImageView moviePoster;


        public ViewHolder(View itemView) {
            super(itemView);
            moviePoster=(ImageView) itemView.findViewById(R.id.movie_poster);

        }
    }


}
