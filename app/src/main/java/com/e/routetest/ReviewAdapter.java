package com.e.routetest;


import android.content.Context;
import android.media.Rating;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import static com.e.routetest.MapsFragment.mMap;
import static com.e.routetest.RouteActivity.arrivals;
import static com.e.routetest.RouteActivity.departures;
import static com.e.routetest.RouteActivity.spots;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.Holder> {
    Context context;
    private List<ReviewData> reviewData = new ArrayList<>();

    public ReviewAdapter(Context context, List<ReviewData> reviewData){
        this.context=context;
        this.reviewData=reviewData;
    }

    @NonNull
    @Override
    public ReviewAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_rv_item,parent,false);
        ReviewAdapter.Holder holder = new ReviewAdapter.Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.Holder holder, int position) {
        holder.textView.setText(reviewData.get(position).getSpotName());
        holder.ratingBar.setRating(reviewData.get(position).getScore());
        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                reviewData.get(position).setScore(holder.ratingBar.getRating());
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewData.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView textView;
        RatingBar ratingBar;

        public Holder(View view){
            super(view);
            textView = view.findViewById(R.id.rvSpotName);
            ratingBar = view.findViewById(R.id.rvRatingBar);
        }
    }
}
