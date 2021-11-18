package com.e.routetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.e.routetest.LoadingActivity.sv;
import static com.e.routetest.LoadingActivity.allSpotList;

import static com.e.routetest.LoginActivity.userId;

public class ShowBoardActivity extends AppCompatActivity {

    ArrayList<String> board =new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent boardintent = getIntent();
        int boardId = boardintent.getIntExtra("1",0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_board);
        TextView titleView1 = (TextView)findViewById(R.id.titleView);
        TextView userView1 = (TextView)findViewById(R.id.userView);
        TextView dateView1 = (TextView)findViewById(R.id.showDate);
        TextView contentView1 = (TextView)findViewById(R.id.viewContent);
        TextView firstSpot1 = (TextView)findViewById(R.id.firstSpot);
        TextView finalSpot1 = (TextView)findViewById(R.id.finalSpot);
        Button uButton = (Button)findViewById(R.id.updateButton);
        Button dButton = (Button)findViewById(R.id.deleteButton);
        Button jButton = (Button)findViewById(R.id.joinButton);
        new Thread(){
            public void run(){
                if(getBoard(boardId)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
                            if(userId.equals(userView1.getText().toString())){
                                jButton.setVisibility(View.INVISIBLE);
                            }
                            else {
                                uButton.setVisibility(View.INVISIBLE);
                                dButton.setVisibility(View.INVISIBLE);
                            }

                        }
                    });
                }
            }
        }.start();


        uButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        dButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    public void run(){
                        deleteBoard(boardId);
                        finish();
                    }
                }.start();
            }
        });

        jButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                new Thread(){
                    public void run(){
                        if(joinAttempt(titleView1.getText().toString(),userView1.getText().toString(),boardId)){
                            System.out.println("신청완료");
                            AlertDialog.Builder builder = new AlertDialog.Builder(ShowBoardActivity.this);
                            builder.setTitle("신청완료");
                            builder.setMessage("신청이 완료되었습니다.");
                            builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    builder.create().show();
                                }
                            });

                        }
                        else {
                            System.out.println("신청실패");
                            AlertDialog.Builder builder = new AlertDialog.Builder(ShowBoardActivity.this);
                            builder.setTitle("오류발생");
                            builder.setMessage("오류가 발생했습니다.");
                            builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    builder.create().show();
                                }
                            });
                        }
                    }
                }.start();
            }
        });


    }
    public boolean getBoard(int n){
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

            String url2 = sv+"getRoute.jsp?routeID="+routeID;
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
            return true;

        }catch (Exception e){
            e.printStackTrace();
        }

        return false;

    }

    public boolean deleteBoard(int n){
        try {
            String url = sv + "deleteBoard.jsp?boardID="+n;
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
            return true;

        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }
    public boolean joinAttempt(String title, String writer,int n){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://route-f81c2-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
        String st = "AAAALpTg_A4:APA91bE_gvdBf4BT6Hm4r2oeGaJgdrbeLt_-j8tSiXrsh8kfH6ooigsCbaKqOo6GxrR0be6VyYsAS3vyzY5YSwgVcdj3RZEjwP5fKryDFbAMxdtXp_vazwHYlSzBuAYNALGuuqKsyJsX";
        final String[] tt = {""};
        databaseReference.child("users").child(writer).child("token").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    System.out.println(task.getException().toString());
                }
                else{
                    System.out.println(task.getResult().getValue().toString());
                    tt[0] =task.getResult().getValue().toString();
                }
            }
        });
        while (tt[0].equals("")){
            System.out.println("토큰 수신중");
        }
        System.out.println(tt[0]);
        if(!joinRequest(n)){
            return false;
        }


        JsonObject jsonObject = new JsonObject();
        JsonObject tJsonObject = new JsonObject();
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(tt[0]);
        JsonObject nJsonObject = new JsonObject();
        jsonObject.add("to",jsonElement);
        nJsonObject.addProperty("title","여행 참가신청");
        nJsonObject.addProperty("body", "회원님이 작성하신 ["+title+"] 게시물에 ["+ userId+"] 회원님이 참가 신청 하셨습니다.");
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

            return true;
        } catch (IOException e){
            System.out.println(e);
        }
        return false;
    }
    public boolean joinRequest(int j) {

        boolean status = false;
        try {
            String url = sv + "joinReqest.jsp?boardID="+j+"&userID="+userId;
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
            String msg = jsonObject.get("msg").getAsString();
            System.out.println(msg);
            if(success.equals("true")) {
                status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

}