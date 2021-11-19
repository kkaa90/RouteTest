package com.e.routetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.e.routetest.LoadingActivity.allSpotList;
import static com.e.routetest.LoadingActivity.sv;
import static com.e.routetest.LoginActivity.userId;

public class O2MActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    O2MAdapter o2MAdapter;
    public static List<O2MRequest> o2MRequest;
    public static Context oContext;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://route-f81c2-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o2_m);
        Button button = (Button)findViewById(R.id.o2mBack);
        oContext=this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        o2MRequest = new ArrayList<>();
        new Thread(){
            public void run(){
                if(getNotify()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            o2MAdapter.notifyDataSetChanged();
                        }
                    });
                }
                else {
                    System.out.println("O2M DB 오류");
                    finish();
                }
            }
        }.start();
        recyclerView = (RecyclerView)findViewById(R.id.o2mRv);
        o2MAdapter = new O2MAdapter(getApplicationContext(),o2MRequest);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(o2MAdapter);
    }
    public boolean getNotify() {
        int boardID;
        String boardTitle;
        String spot1="";
        String spot2="";
        String uI;
        String nickName;
        String date;
        final String[] gender = new String[1];
        final int[] age = new int[1];
        try {
            String url = sv + "notifyApplicant.jsp?userID="+userId;
            System.out.println(url);


            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(url).get();
            Request request = builder.build();

            Response response = client.newCall(request).execute();
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(response.body().string());
            JsonObject jsonObject = jsonElement.getAsJsonObject();


            JsonArray row = jsonObject.get("items").getAsJsonArray();
            //System.out.println(row.size());
            for (int i = 1; i < row.size(); i++) {
                age[0]=0;
                JsonObject jsonObject1 = (JsonObject) row.get(i);
                //System.out.println(jsonObject1);
                boardID = jsonObject1.get("boardID").getAsInt();
                boardTitle = jsonObject1.get("boardTitle").getAsString();
                String sArr[]=getBoard(boardID).split(",");
                for(Spot obj : allSpotList){
                    if(obj.spotID==Integer.parseInt(sArr[0])){
                        spot1=obj.spotName;
                    }
                    if(obj.spotID==Integer.parseInt(sArr[sArr.length-2])){
                        spot2=obj.spotName;
                    }
                }
                date = sArr[sArr.length-1];
                uI = jsonObject1.get("userID").getAsString();
                nickName = jsonObject1.get("nickName").getAsString();
                databaseReference.child("users").child(uI).child("gender").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        gender[0] = task.getResult().getValue().toString();
                    }
                });
                databaseReference.child("users").child(uI).child("age").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        age[0] =Integer.parseInt(task.getResult().getValue().toString());
                    }
                });
                while (age[0]==0){
                    System.out.println("값 갱신 대기");
                }
                try{
                    o2MRequest.add(new O2MRequest(boardID,boardTitle,spot1,spot2,uI,nickName, gender[0],date, age[0]));
                }
                catch (Exception e1){
                    return false;
                }

                //System.out.println(i);
            }
            return true;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public String getBoard(int n){
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
            routeList+=(","+jsonObject.get("appliT").getAsString());
            return routeList;

        }catch (Exception e){
            e.printStackTrace();
        }

        return "";

    }
}