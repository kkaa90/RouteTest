package com.e.routetest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.e.routetest.MainActivity.sv;
import static com.e.routetest.SearchFragment.allSpotList;

public class ShowBoardActivity extends AppCompatActivity {

    ArrayList<String> board =new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent boardintent = getIntent();
        int boardId = boardintent.getIntExtra("1",0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_board);
        new Thread(){
            public void run(){
                getBoard(boardId);
            }
        }.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView titleView1 = (TextView)findViewById(R.id.titleView);
                TextView userView1 = (TextView)findViewById(R.id.userView);
                TextView dateView1 = (TextView)findViewById(R.id.showDate);
                TextView contentView1 = (TextView)findViewById(R.id.viewContent);
                TextView firstSpot1 = (TextView)findViewById(R.id.firstSpot);
                TextView finalSpot1 = (TextView)findViewById(R.id.finalSpot);
                String[] spotArr = board.get(4).split(",");
                for(Spot obj : allSpotList){
                    if(obj.spotID==Integer.parseInt(spotArr[0])){
                        firstSpot1.setText(obj.spotName);
                    }
                    if(obj.spotID==Integer.parseInt(spotArr[spotArr.length-1])){
                        finalSpot1.setText(obj.spotName);
                    }
                }
                titleView1.setText(board.get(0));
                userView1.setText(board.get(1));
                dateView1.setText(board.get(2));
                contentView1.setText(board.get(3));
            }
        }, 2000);

    }




    public void getBoard(int n){


        try {
            String url = sv + "getBoard.jsp?boardID="+n;
            System.out.println(url);


            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(url).get();
            Request request = builder.build();

            Response response = client.newCall(request).execute();
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(response.body().string());
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String title = jsonObject.get("boardTitle").getAsString();
            board.add(title);
            String user = jsonObject.get("userID").getAsString();
            board.add(user);
            String date = jsonObject.get("appliT").getAsString();
            board.add(date);
            String content = jsonObject.get("boardContent").getAsString();
            board.add(content);
            String routeID = jsonObject.get("routeID").getAsString();

            String url2 = "http://13.125.252.236:8080/teamproject/getRoute.jsp?routeID="+routeID;
            System.out.println(url2);

            OkHttpClient client2 = new OkHttpClient();
            Request.Builder builder2 = new Request.Builder().url(url2).get();
            Request request2 = builder2.build();

            Response response2 = client2.newCall(request2).execute();
            Gson gson2 = new Gson();
            JsonParser jsonParser2 = new JsonParser();
            JsonElement jsonElement2 = jsonParser2.parse(response2.body().string());
            JsonObject jsonObject2 = jsonElement2.getAsJsonObject();
            String routeList = jsonObject2.get("routeList").getAsString();
            board.add(routeList);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}