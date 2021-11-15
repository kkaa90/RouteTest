package com.e.routetest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.e.routetest.LoginActivity.age;
import static com.e.routetest.LoginActivity.gender;
import static com.e.routetest.LoginActivity.nN;
import static com.e.routetest.LoginActivity.userId;
import static com.e.routetest.MainActivity.token;
public class MyMenuActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://route-f81c2-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_menu);
        Button bButton = (Button)findViewById(R.id.backButton);
        bButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Button test = (Button)findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("users").child(userId).child("nickName").setValue(nN);
                databaseReference.child("users").child(userId).child("gender").setValue(gender);
                databaseReference.child("users").child(userId).child("age").setValue(age);
                databaseReference.child("users").child(userId).child("token").setValue(token);
                System.out.println("nickName : "+nN+" gender : "+gender+" age : " +age );
            }
        });
        Button test2 = (Button)findViewById(R.id.test2);
        test2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    public void run(){
                        testF();
                    }
                }.start();
            }
        });
        TextView iTextView = (TextView)findViewById(R.id.idView);
        iTextView.setText(userId);
        TextView nTextView = (TextView)findViewById(R.id.nNView);
        nTextView.setText(nN);

    }
    public void testF(){
        String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
        String st = "AAAALpTg_A4:APA91bE_gvdBf4BT6Hm4r2oeGaJgdrbeLt_-j8tSiXrsh8kfH6ooigsCbaKqOo6GxrR0be6VyYsAS3vyzY5YSwgVcdj3RZEjwP5fKryDFbAMxdtXp_vazwHYlSzBuAYNALGuuqKsyJsX";
        String tt = "ftpt0KtYRM6y8hNvUzmHDl:APA91bGRrOGYkW0KwsuvwDOcAPbjQQV8x5sv1wBLqbNiK1A3a__9wIujAqCG-oHDkx6ah9yzH-y5OrmfHkPRlj9wxfg3cBc-SWdPBght5ah9BpPecpvH9LAhCvWH7XkOKSIeXn7dfXnn";
        JsonObject jsonObject = new JsonObject();
        JsonObject tJsonObject = new JsonObject();
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(tt);
        JsonObject nJsonObject = new JsonObject();
        jsonObject.add("to",jsonElement);
        nJsonObject.addProperty("title","title");
        nJsonObject.addProperty("body", "body");
        jsonObject.add("notification",nJsonObject);

        System.out.println(jsonObject.toString());
        final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        try {
            Request request = new Request.Builder().url(FCM_MESSAGE_URL)
                    .addHeader("Content-Type","application/json; UTF-8")
                    .addHeader("Authorization","key="+st)
                    .post(RequestBody.create(mediaType, jsonObject.toString())).build();
            Response response = okHttpClient.newCall(request).execute();


            String res = response.toString();
            System.out.println(res);
        } catch (IOException e){
            System.out.println(e);
        }
        return;
    }
}