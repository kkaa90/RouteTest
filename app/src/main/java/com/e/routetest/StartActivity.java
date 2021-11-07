package com.e.routetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class StartActivity extends AppCompatActivity {
    static String sv = "http://13.209.21.35:8080/teamproject/";
    public static ArrayList<Spot> allSpotList = new ArrayList<Spot>();
    public static ArrayList<String> allSpotName = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Button button = (Button)findViewById(R.id.goLogin);

        AppDatabase db = AppDatabase.getInstance(this);
        new Thread() {
            public void run() {
                getSpot();
            }
        }.start();



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
        Button button1= (Button)findViewById(R.id.goTest);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ShowBoardActivity.class);
                startActivity(intent);
            }
        });

        Button button2= (Button)findViewById(R.id.goTest2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RouteActivity.class);
                startActivity(intent);
            }
        });

    }
    public int getSpot() {
        int spotId;
        String title;
        double x;
        double y;
        String address;
        int status = 0;
        try {
            String url = sv + "viewAttraction.jsp";
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
                allSpotList.add(new Spot(spotId, title, y, x, address));
                allSpotName.add(title);
                //System.out.println(i);
            }
            status = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

}