package com.example.abdoamin.movieappudacity.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.abdoamin.movieappudacity.R;
import com.example.abdoamin.movieappudacity.myObject.Review;
import com.example.abdoamin.movieappudacity.myObject.Trailers;

import java.util.List;


/**
 * Created by Abdo Amin on 9/15/2017.
 */

public class TrailersRecycleAdapter extends RecyclerView.Adapter<TrailersRecycleAdapter.ViewHolder> {

    private List<Trailers> mTrailers;
    private int mTrailersLayout;
    private Context context;

    public TrailersRecycleAdapter(List<Trailers> mTrailers, int mTrailersLayout, Context context) {
        this.mTrailers = mTrailers;
        this.mTrailersLayout = mTrailersLayout;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mTrailersLayout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.name.setText(mTrailers.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent appYoutube = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + mTrailers.get(position).getKey()));
                Intent webYoutube = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.youtube.com/watch?v=" + mTrailers.get(position).getKey()));
                try {
                    if (appYoutube.resolveActivity(context.getPackageManager()) != null)
                        context.startActivity(appYoutube);
                } catch (ActivityNotFoundException ex) {
                    if (webYoutube.resolveActivity(context.getPackageManager()) != null)
                        context.startActivity(webYoutube);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.trailer_name);

        }
    }


}
