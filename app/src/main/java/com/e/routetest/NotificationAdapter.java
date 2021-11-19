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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import static com.e.routetest.MapsFragment.mMap;
import static com.e.routetest.RouteActivity.arrivals;
import static com.e.routetest.RouteActivity.departures;
import static com.e.routetest.RouteActivity.spots;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Holder> {
    private Context context;
    private List<Notify> notifyList = new ArrayList<>();

    public NotificationAdapter(Context context, List<Notify> notifyList){
        this.context = context;
        this.notifyList = notifyList;
    }

    @NonNull
    @Override
    public NotificationAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_rv_item,parent,false);
        NotificationAdapter.Holder holder = new NotificationAdapter.Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.Holder holder, int position) {
        holder.imageView.setImageResource(R.drawable.ic_notifications_black_24dp);
        if(notifyList.get(position).getType()==1){
            holder.nType.setText("여행신청");
        }
        else {
            holder.nType.setText("이상발생");
        }
        holder.nContent.setText(notifyList.get(position).getBody());
    }

    @Override
    public int getItemCount() {
        return notifyList.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView nType;
        public TextView nContent;
        public Holder(View view){
            super(view);
            imageView=(ImageView) view.findViewById(R.id.nImageView);
            nType=(TextView) view.findViewById(R.id.nTypeView);
            nContent=(TextView) view.findViewById(R.id.nContentView);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos!= RecyclerView.NO_POSITION){
                        if(nType.getText().equals("여행신청")){
                            Intent intent = new Intent(view.getContext(),O2MActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(view.getContext(),TripAlarmListActivity.class);
                            view.getContext().startActivity(intent);
                        }
                    }
                }
            });
        }
    }
}
