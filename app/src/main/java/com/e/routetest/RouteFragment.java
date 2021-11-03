package com.e.routetest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class RouteFragment extends Fragment {
    private final String DEFAULT = "DEFAULT";
    public static ArrayList<Route> routes= new ArrayList<Route>();
    public static ArrayList<Spot> spots=new ArrayList<Spot>();
    public static ArrayList<Integer> departures=new ArrayList<Integer>();
    public static ArrayList<Integer> arrivals=new ArrayList<Integer>();
    public static ArrayList<Integer> noticeTime = new ArrayList<Integer>();
    public static Context context_main;
    public int var;
    public static int warning =0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_route, container, false);
        ViewPager vf = view.findViewById(R.id.viewPager);
        TabLayout tl = view.findViewById(R.id.tabLayout);
        PagerAdapter adapter=new ViewPageAdapter(getChildFragmentManager(), tl.getTabCount());
        vf.setAdapter(adapter);
        vf.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tl));
        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                vf.setCurrentItem(tab.getPosition());
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab){

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab){

            }
        });


        return view;
    }

    public int getTime(Spot A, Spot B){
        int arrtime=-1;
        double ax=A.spotX;
        double ay=A.spotY;
        double bx=B.spotX;
        double by=B.spotY;

        /*System.out.println("ax : "+ax);
        System.out.println("ay : "+ay);
        System.out.println("bx : "+bx);
        System.out.println("by : "+by);*/
        try {
            String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&mode=transit&origins="
                    +ax+","+ay+"&destinations="+bx+","+by+"&region=KR&key=AIzaSyAn-Tk1TUWDZWTHbdshVkc9z2uQG4dULNQ";
            //String url = "http://3.34.178.98:8080/teamproject/viewAttaction.jsp";
            System.out.println(url);


            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(url).get();
            Request request = builder.build();

            Response response = client.newCall(request).execute();
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(response.body().string());
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            JsonArray row = jsonObject.get("rows").getAsJsonArray();
            JsonObject jsonObject1 = (JsonObject) row.get(0);
            JsonArray row2 = jsonObject1.get("elements").getAsJsonArray();
            JsonObject jsonObject2 = (JsonObject) row2.get(0);
            JsonObject jsonObject3 = jsonObject2.get("duration").getAsJsonObject();
            arrtime = jsonObject3.get("value").getAsInt();

            System.out.println(arrtime);

        }catch (Exception e){
            e.printStackTrace();
        }
        return arrtime;
    }
}