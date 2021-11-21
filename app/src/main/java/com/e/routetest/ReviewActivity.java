package com.e.routetest;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.e.routetest.LoadingActivity.sv;
import static com.e.routetest.LoginActivity.userId;

public class ReviewActivity extends AppCompatActivity {
    public List<ReviewData> reviewData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        RecyclerView recyclerView = findViewById(R.id.routeReviewRV);
        reviewData = new ArrayList<>();
        //reviewData.add(new ReviewData(126078,"광안리해수욕장",0.0f));
        //reviewData.add(new ReviewData(126079,"다대포해수욕장",0.0f));
        //reviewData.add(new ReviewData(126080,"송정해수욕장",0.0f));
        //reviewData.add(new ReviewData(126081,"해운대해수욕장",0.0f));
        ReviewAdapter reviewAdapter = new ReviewAdapter(getApplicationContext(),reviewData);


        //임시저장경로에서 경로 받아오기
        TRouteDataBase tDb = TRouteDataBase.getInstance(getApplicationContext());
        new Thread(){
            public void run(){
                List<TRoute> tRouteList = new ArrayList<>(tDb.tRouteRepository().findAll());
                reviewData.clear();
                if(!tRouteList.isEmpty()){
                    String[] placeIDs = tRouteList.get(0).getPlaceIDs().split(",");
                    String[] placeNames = tRouteList.get(0).getPlaceNames().split(",");

                    for(int i=0;i<placeIDs.length;i++){
                        reviewData.add(new ReviewData(Integer.parseInt(placeIDs[i]),placeNames[i],0.0f));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            reviewAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }.start();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(reviewAdapter);

        Button rButton = (Button)findViewById(R.id.sendRVButton);
        rButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    public void run(){
                        for(int i=0;i<reviewData.size();i++){
                            if(review(userId,Integer.toString(reviewData.get(i).getSpotId()),(int)reviewData.get(i).getScore())){

                            }
                        }
                    }
                }.start();
            }
        });
    }
    public boolean review(String id, String sId, int score) {
        try {
            String url = sv + "writeReview.jsp?userID="+id+"&attractionID="+sId+"&score="+score;
            System.out.println(url);


            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(url).get();
            Request request = builder.build();

            Response response = client.newCall(request).execute();
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(response.body().string());
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String success = jsonObject.get("success").getAsString();
            System.out.println(success);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}