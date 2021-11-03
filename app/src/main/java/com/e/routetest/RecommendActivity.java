package com.e.routetest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.e.routetest.StartActivity.sv;
import static com.e.routetest.RouteActivity.spots;
import static com.e.routetest.RouteActivity.warning;

public class RecommendActivity extends AppCompatActivity {
    public ArrayList<Spot> rSpot = new ArrayList<Spot>();
    public ArrayList<Spot> wSpot = new ArrayList<Spot>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        wSpot.add(spots.get(warning));
        RecyclerView viewChange = findViewById(R.id.viewChange);
        RecyclerView viewRecommend = findViewById(R.id.viewRecommend);
        viewChange.setHasFixedSize(true);
        viewRecommend.setHasFixedSize(true);
        ViewRecommendAdapter viewSpotAdapter = new ViewRecommendAdapter(getApplicationContext(),wSpot);
        ViewRecommendAdapter viewRecommendAdapter = new ViewRecommendAdapter(getApplicationContext(),rSpot);
        viewChange.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        viewRecommend.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        viewChange.setAdapter(viewSpotAdapter);
        viewRecommend.setAdapter(viewRecommendAdapter);
        new Thread(){
            public void run(){
                getSpot(wSpot.get(0));
            }
        }.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewSpotAdapter.notifyDataSetChanged();
                viewRecommendAdapter.notifyDataSetChanged();
            }
        }, 1000);

    }

    public void getSpot(Spot spot) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
}