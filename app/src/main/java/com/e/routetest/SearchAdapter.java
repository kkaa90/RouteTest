package com.e.routetest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
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

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.Holder> implements Filterable {
    private Context context;
    private List<Spot> allSpotList=new ArrayList<>();
    private List<Spot> filteredList = new ArrayList<>();

    public SearchAdapter(Context context, List<Spot> allSpotList){
        this.context=context;
        this.allSpotList=allSpotList;
        this.filteredList=filteredList;
    }
    @NonNull
    @Override
    public SearchAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spot_rv_item,parent,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.Holder holder, int position) {
        int itemPosition = position;
        //holder.imageView2.setImageResource(R.drawable.photo1);
        holder.textSpotName.setText(filteredList.get(itemPosition).spotName);
        holder.textSpotAddress.setText(filteredList.get(itemPosition).spotAddress);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String string = charSequence.toString();
                if(string.equals("")){
                    filteredList=allSpotList;
                }
                else{
                    ArrayList<Spot> filteringList=new ArrayList<>();
                    for(Spot obj : allSpotList){
                        if(obj.spotName.contains(string)){
                            filteringList.add(obj);
                        }
                    }
                    filteredList=filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values=filteredList;
                for(int i=0 ; i<filteredList.size();i++){
                    System.out.println(filteredList.get(i).spotName);
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<Spot>)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class Holder extends RecyclerView.ViewHolder{
        //ImageView imageView2;
        TextView textSpotName;
        TextView textSpotAddress;
        public Holder(View view){
            super(view);
            //imageView2 = (ImageView)view.findViewById(R.id.spotImage2);
            textSpotName=(TextView)view.findViewById(R.id.spotName2);
            textSpotAddress=(TextView)view.findViewById(R.id.spotAddress);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        Spot now = filteredList.get(pos);
                        if(spots.size()<8) {
                            spots.add(new Spot(now.spotID, now.spotName, now.spotX, now.spotY, now.spotAddress));
                            arrivals.add(-1);
                            departures.add(-1);
                            mMap.clear();
                            for(int i=0; i<spots.size();i++) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                Spot spotNow = spots.get(i);
                                markerOptions.position(new LatLng(spotNow.getSpotX(), spotNow.getSpotY())).title(i + 1 + " : " + spotNow.spotName);
                                mMap.addMarker(markerOptions);
                            }
                        }
                        else{
                            Toast toast = Toast.makeText(context,"최대 여행지 입니다.",Toast.LENGTH_SHORT);
                        }
                    }
                }
            });
        }
    }
}
