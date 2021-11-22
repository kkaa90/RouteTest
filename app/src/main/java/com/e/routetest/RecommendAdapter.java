package com.e.routetest;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.e.routetest.RecommendActivity.rContext;

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.Holder> {


    private Context context;
    private List<Spot> allSpotList=new ArrayList<>();

    public RecommendAdapter(Context context, List<Spot> allSpotList) {
        this.context = context;
        this.allSpotList = allSpotList;
    }

    @NonNull
    @Override
    public RecommendAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spot_rv_item,parent,false);
        RecommendAdapter.Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendAdapter.Holder holder, int position) {
        int itemPosition = position;
        holder.imageView2.setImageResource(R.drawable.photo1);
        holder.textSpotName.setText(allSpotList.get(itemPosition).spotName);
        holder.textSpotAddress.setText(allSpotList.get(itemPosition).spotAddress);
    }

    @Override
    public int getItemCount() {
        return allSpotList.size();
    }
    public class Holder extends RecyclerView.ViewHolder{
        ImageView imageView2;
        TextView textSpotName;
        TextView textSpotAddress;
        public Holder(View view){
            super(view);
            imageView2 = (ImageView)view.findViewById(R.id.spotImage2);
            textSpotName=(TextView)view.findViewById(R.id.spotName2);
            textSpotAddress=(TextView)view.findViewById(R.id.spotAddress);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        Intent intent = new Intent();
                        intent.putExtra("spotId",allSpotList.get(pos).spotID);
                        intent.putExtra("now", RecommendActivity.now);
                        ((RecommendActivity)rContext).setResult(RESULT_OK,intent);
                        ((RecommendActivity)rContext).finish();
                    }
                }
            });
        }
    }
}
