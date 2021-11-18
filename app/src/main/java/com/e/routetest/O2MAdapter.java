package com.e.routetest;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class O2MAdapter extends RecyclerView.Adapter<O2MAdapter.Holder> {
    private Context context;
    private List<O2MRequest> o2MRequests = new ArrayList<>();
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private int prePosition = -1;

    public O2MAdapter(Context context, List<O2MRequest> o2MRequests) {
        this.context = context;
        this.o2MRequests = o2MRequests;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.o2m_rv_item,parent,false);
        O2MAdapter.Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        int itemPosition=position;
        holder.titleView.setText(o2MRequests.get(itemPosition).boardTitle);
        holder.idView.setText(o2MRequests.get(itemPosition).userID);
        holder.nNView.setText(o2MRequests.get(itemPosition).nickName);
        holder.genderView.setText(o2MRequests.get(itemPosition).gender);
        holder.ageView.setText(Integer.toString(o2MRequests.get(itemPosition).age));
        holder.spot1View.setText(o2MRequests.get(itemPosition).spot1);
        holder.spot2View.setText(o2MRequests.get(itemPosition).spot2);
        holder.goBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return o2MRequests.size();
    }


    public class Holder extends RecyclerView.ViewHolder{
        TextView titleView;
        TextView idView;
        TextView nNView;
        TextView genderView;
        TextView ageView;
        TextView spot1View;
        TextView spot2View;
        Button goBoardButton;
        Button acceptButton;
        Button rejectButton;
        ConstraintLayout eC;
        TextView exView;
        public Holder(View view){
            super(view);
            titleView = (TextView)view.findViewById(R.id.o2mTitleView);
            idView = (TextView)view.findViewById(R.id.o2mIdView);
            nNView = (TextView)view.findViewById(R.id.o2mNNView);
            genderView = (TextView)view.findViewById(R.id.o2mGenderView);
            ageView = (TextView)view.findViewById(R.id.o2mAgeView);
            spot1View = (TextView)view.findViewById(R.id.o2mFSView);
            spot2View = (TextView)view.findViewById(R.id.o2mLSView);
            goBoardButton = (Button)view.findViewById(R.id.o2MGoBoardButton);
            acceptButton = (Button)view.findViewById(R.id.o2mAcceptButton);
            rejectButton = (Button)view.findViewById(R.id.o2mRejectButton);
            eC = (ConstraintLayout)view.findViewById(R.id.o2mEC);
            exView = (TextView)view.findViewById(R.id.o2MEx);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(selectedItems.get(pos)){
                        selectedItems.delete(pos);
                        eC.setVisibility(View.GONE);
                        exView.setVisibility(View.VISIBLE);
                    }
                    else{
                        selectedItems.put(pos,true);
                        eC.setVisibility(View.VISIBLE);
                        exView.setVisibility(View.GONE);
                    }

                }
            });
        }

    }

}
