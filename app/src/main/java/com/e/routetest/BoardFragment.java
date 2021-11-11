package com.e.routetest;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static com.e.routetest.LoadingActivity.allSpotList;
import static com.e.routetest.LoadingActivity.sv;

public class BoardFragment extends Fragment {
    ArrayList<Board> boards = new ArrayList<Board>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.boardView);
        ViewBoardAdapter viewBoardAdapter = new ViewBoardAdapter(getActivity().getApplicationContext(),boards);
        Button goWriteBoard = (Button) view.findViewById(R.id.writeBoardButton);
        goWriteBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(),WriteBoardActivity.class);
                startActivity(intent);
            }
        });

        new Thread(){
            public void run(){
                if(getSpot()){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewBoardAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }.start();
        Button refresh = (Button)view.findViewById(R.id.btnApply);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewBoardAdapter.notifyDataSetChanged();
                for(int i=0;i<boards.size();i++){
                    System.out.println(boards.get(i).boardTitle);

                }
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(viewBoardAdapter);

        return view;
    }
    public boolean getSpot() {
        ViewBoardAdapter viewBoardAdapter = new ViewBoardAdapter(getActivity().getApplicationContext(),boards);
        try {
            String url = sv + "viewBoard.jsp?pageNumber=1";
            System.out.println(url);


            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(url).get();
            Request request = builder.build();

            Response response = client.newCall(request).execute();
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(response.body().string());
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            JsonArray jsonArray = jsonObject.get("boards").getAsJsonArray();
            for(int i=0;i<jsonArray.size();i++){
                JsonObject jsonObject1 = jsonArray.get(i).getAsJsonObject();
                int boardID=jsonObject1.get("boardID").getAsInt();
                String boardTitle = jsonObject1.get("boardTitle").getAsString();
                String temp= boardTitle;
                if(boardTitle.length()>8){
                    temp = boardTitle.substring(0,6);
                    temp=temp +"...";
                }
                String userID=jsonObject1.get("userID").getAsString();
                String nickName=userID;
                String destiny = " ";
                String arrival = " ";
                int themeID=0;
                int routeID = jsonObject1.get("routeID").getAsInt();
                System.out.println(routeID);
                if(routeID==0) continue;
                String[] spotArr = getRoute(routeID).split(",");
                int find1=0,find2=0;
                for(Spot obj : allSpotList){
                    if(obj.spotID==Integer.parseInt(spotArr[0])){
                        find1=1;
                        destiny=obj.spotName;
                    }
                    if(obj.spotID==Integer.parseInt(spotArr[spotArr.length-1])){
                        find2=1;
                        arrival=obj.spotName;
                    }
                }
                if(find1==0) destiny="-";
                if(find2==0) arrival="-";
                String boardDate = jsonObject1.get("boardDate").getAsString();
                int currentP= jsonObject1.get("currentP").getAsInt();
                int maxP=jsonObject1.get("maxP").getAsInt();
                String appliT=jsonObject1.get("appliT").getAsString();
                boards.add(new Board(boardID,temp,userID,nickName,destiny,arrival,themeID,routeID,boardDate,currentP,maxP,appliT));
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public String getRoute(int routeID){
        String rL="";
        try {
            String url = sv + "getRoute.jsp?routeID="+String.valueOf(routeID);
            System.out.println(url);


            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(url).get();
            Request request = builder.build();

            Response response = client.newCall(request).execute();
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(response.body().string());
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            rL=jsonObject.get("routeList").getAsString();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return rL;
    }
}