package com.example.abdoamin.movieappudacity.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abdoamin.movieappudacity.MovieDetails;
import com.example.abdoamin.movieappudacity.R;
import com.example.abdoamin.movieappudacity.myObject.Movie;
import com.example.abdoamin.movieappudacity.myObject.Review;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Abdo Amin on 9/15/2017.
 */

public class ReviewRecycleAdapter extends RecyclerView.Adapter<ReviewRecycleAdapter.ViewHolder> {

    private List<Review> mReviews;
    private int movieLayout;
    private Context context;

    public ReviewRecycleAdapter(List<Review> mReviews, int movieLayout, Context context) {
        this.mReviews = mReviews;
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
        holder.author.setText(context.getString( R.string.review_author)+" "+mReviews.get(position).getAuthor());
        holder.review.setText(mReviews.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
          public TextView author;
          public TextView review;

        public ViewHolder(View itemView) {
            super(itemView);
            author= (TextView) itemView.findViewById(R.id.review_auther);
            review=(TextView) itemView.findViewById(R.id.review_review);

        }
    }


}
