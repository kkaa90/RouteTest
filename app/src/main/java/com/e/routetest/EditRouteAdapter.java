package com.e.routetest;

import android.content.Context;
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

public class EditRouteAdapter extends RecyclerView.Adapter<EditRouteAdapter.Holder> {
    private Context context;
    private List<Spot> list =new ArrayList<>();
    private List<Integer> depList = new ArrayList<>();
    private List<Integer> arrList = new ArrayList<>();

    public EditRouteAdapter(Context context, List<Spot> list, List<Integer> depList, List<Integer> arrList){
        this.context=context;
        this.list=list;
        this.depList=depList;
        this.arrList=arrList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_rv_item,parent,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        int itemPosition=position;
        //holder.imageView.setImageResource(R.drawable.photo1);
        holder.spotName.setText(list.get(itemPosition).spotName);

        String arr=String.valueOf(arrList.get(itemPosition)/3600)+ ":";
        if(arrList.get(itemPosition)/3600<10) arr = "0"+arr;
        if(((arrList.get(itemPosition)%3600)/60) < 10) arr = arr + "0";
        arr=arr+ String.valueOf((arrList.get(itemPosition)%3600)/60);
        String dep=String.valueOf(depList.get(itemPosition)/3600)+ ":";
        if(depList.get(itemPosition)/3600<10) dep = "0"+dep;
        if(((depList.get(itemPosition)%3600)/60) < 10) dep = dep + "0";
        dep=dep+ String.valueOf((depList.get(itemPosition)%3600)/60);
        holder.arrivalText.setText(arr);
        holder.departureText.setText(dep);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        //public ImageView imageView;
        public TextView spotName;
        public TextView departureText;
        public TextView arrivalText;
        public Holder(View view){
            super(view);
            //imageView=(ImageView) view.findViewById(R.id.spotImage2);
            spotName=(TextView) view.findViewById(R.id.spotName2);
            departureText=(TextView) view.findViewById(R.id.viewDeparture);
            arrivalText=(TextView) view.findViewById(R.id.viewArrival);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos!= RecyclerView.NO_POSITION){
                        spots.remove(pos);
                        arrivals.remove(pos);
                        departures.remove(pos);
                        mMap.clear();
                        for(int i=0; i<spots.size();i++) {
                            MarkerOptions markerOptions = new MarkerOptions();
                            Spot spotNow = spots.get(i);
                            markerOptions.position(new LatLng(spotNow.getSpotX(), spotNow.getSpotY())).title(i + 1 + " : " + spotNow.spotName);
                            mMap.addMarker(markerOptions);
                        }
                        notifyDataSetChanged();
                    }
                }
            });

        }
    }
}
