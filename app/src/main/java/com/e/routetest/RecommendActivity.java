package com.e.routetest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.e.routetest.LoadingActivity.allSpotList;
import static com.e.routetest.LoadingActivity.sv;
import static com.e.routetest.RouteActivity.spots;
import static com.e.routetest.RouteActivity.warning;

public class RecommendActivity extends AppCompatActivity {
    public ArrayList<Spot> rSpot = new ArrayList<Spot>();
    Spot n;
    public static Context rContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        rContext = this;
        Intent spotIntent = getIntent();
        int spotId = spotIntent.getIntExtra("spotId",0);
        int routeId = spotIntent.getIntExtra("routeId",0);
        int now = spotIntent.getIntExtra("now",0);
        for (Spot obj:allSpotList){
            if(spotId==obj.spotID){
                n=obj;
            }
        }
        ImageView imageView = (ImageView)findViewById(R.id.rSpotImage);
        imageView.setImageResource(R.drawable.photo1);
        TextView textView = (TextView)findViewById(R.id.rSpotName);
        textView.setText(n.getSpotName());
        TextView textView1 = (TextView)findViewById(R.id.rSpotAddress);
        textView1.setText(n.getSpotAddress());
        RecyclerView viewRecommend = findViewById(R.id.viewRecommend);
        viewRecommend.setHasFixedSize(true);
        RecommendAdapter recommendAdapter = new RecommendAdapter(getApplicationContext(),rSpot);
        viewRecommend.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        viewRecommend.setAdapter(recommendAdapter);
        new Thread(){
            public void run(){
                if(getSpot(n)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recommendAdapter.notifyDataSetChanged();
                        }
                    });

                }
            }
        }.start();


    }

    public boolean getSpot(Spot spot) {
        int spotId;
        String title;
        double x;
        double y;
        String address;
        int status = 0;
        try {
            String url = sv + "recommend.jsp?routeID=12&attractionID="+String.valueOf(spot.spotID)+"&mapX="+spot.spotY+"&mapY="+spot.spotX;
            System.out.println(url);


            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(url).get();
            Request request = builder.build();

            Response response = client.newCall(request).execute();
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(response.body().string());
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            JsonArray row = jsonObject.get("attractions").getAsJsonArray();
            for (int i = 0; i < row.size(); i++) {
                JsonObject jsonObject1 = (JsonObject) row.get(i);
                spotId = jsonObject1.get("attractionID").getAsInt();
                title = jsonObject1.get("title").getAsString();
                x = jsonObject1.get("mapX").getAsDouble();
                y = jsonObject1.get("mapY").getAsDouble();
                address = jsonObject1.get("addr").getAsString();
                rSpot.add(new Spot(spotId, title, y, x, address));
                //System.out.println(i);
            }

            status = 1;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}