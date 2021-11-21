package com.e.routetest;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        reviewData.add(new ReviewData(126081,"해운대해수욕장",0.0f));
        ReviewAdapter reviewAdapter = new ReviewAdapter(getApplicationContext(),reviewData);


        //임시저장경로에서 경로 받아오기
        TempPlaceDatabase tDb =TempPlaceDatabase.getInstance(getApplicationContext());//DB객체생성
        reviewData.clear();
        new Thread(){
            public void run(){
                List<TempPlace> tempRoutedata = new ArrayList<TempPlace>(tDb.tempPlaceRepository().findAll());
                if(tempRoutedata!=null) {
                    for (int i = 0; i < tempRoutedata.size(); i++) {
                        reviewData.add(new ReviewData(tempRoutedata.get(i).getPlaceID(), tempRoutedata.get(i).getPlaceName(), 0.0f));
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
                for(int i=0;i<reviewData.size();i++){
                    System.out.println(reviewData.get(i).getSpotName()+" : "+Float.toString(reviewData.get(i).getScore()));
                }
            }
        });
    }
}