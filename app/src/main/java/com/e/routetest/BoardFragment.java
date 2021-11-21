package com.e.routetest;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.e.routetest.LoadingActivity.allSpotList;
import static com.e.routetest.LoadingActivity.sv;

public class BoardFragment extends Fragment {
    ArrayList<Board> boards = new ArrayList<Board>();
    ArrayList<Board> boards2 = new ArrayList<Board>();
    List<Integer> buttons = new ArrayList<>();
    boolean[] checked = new boolean[7];
    BoardAdapter boardAdapter;
    int bs=1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board, container, false);
        MaterialButtonToggleGroup materialButtonToggleGroup = (MaterialButtonToggleGroup)view.findViewById(R.id.toggleTheme);
        boardAdapter = new BoardAdapter(getActivity().getApplicationContext(),boards2);
        materialButtonToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                buttons = materialButtonToggleGroup.getCheckedButtonIds();
                boards2.clear();
                Arrays.fill(checked,false);
                if(buttons.size()==0){
                    boards2.clear();
                    boards2.addAll(boards);
                }
                else {

                    for(int i=0;i<buttons.size();i++){
                        MaterialButton materialButton = materialButtonToggleGroup.findViewById(buttons.get(i));
                        int k = Integer.parseInt(materialButton.getTag().toString());
                        checked[k] = true;
                    }

                    for(int i=0;i<boards.size();i++){
                        if(checked[boards.get(i).themeID]){
                            boards2.add(boards.get(i));
                        }
                    }

                }
//                for(int i=0;i<7;i++){
//                    System.out.println(i+" : " + checked[i]);
//                }
                boardAdapter.notifyDataSetChanged();
            }
        });
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.boardView);

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
                if(getSpot(bs)){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            boards2.addAll(boards);
                            bs++;
                            boardAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }.start();
        Button refresh = (Button)view.findViewById(R.id.btnApply);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boardAdapter.notifyDataSetChanged();
                for(int i=0;i<boards.size();i++){
                    System.out.println(boards.get(i).boardTitle);

                }
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(boardAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!recyclerView.canScrollVertically(1)&&newState==RecyclerView.SCROLL_STATE_IDLE&&bs!=0) {
                    new Thread() {
                        public void run() {
                            if (getSpot(bs)) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        boards2.addAll(boards);
                                        bs++;
                                        boardAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    }.start();
                }
            }
        });
        return view;
    }
    public boolean getSpot(int n) {

        try {
            String url = sv + "viewBoard.jsp?pageNumber="+n;
            System.out.println(url);

            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(url).get();
            Request request = builder.build();

            Response response = client.newCall(request).execute();
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(response.body().string());
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            int now = jsonObject.get("page").getAsInt();
            if(now == 0) {
                bs = 0;
                return false;
            }

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
                    if(obj.spotID==Integer.parseInt(spotArr[spotArr.length-2])){
                        find2=1;
                        arrival=obj.spotName;
                    }
                }
                int themeID=Integer.parseInt(spotArr[spotArr.length-1]);
                if(find1==0) destiny="-";
                if(find2==0) arrival="-";
                String boardDate = jsonObject1.get("boardDate").getAsString();
                int currentP= jsonObject1.get("currentP").getAsInt();
                int maxP=jsonObject1.get("maxP").getAsInt();
                String appliT=jsonObject1.get("appliT").getAsString();
                boards.add(new Board(boardID,temp,userID,nickName,destiny,arrival,themeID,routeID,boardDate,currentP,maxP,appliT));
            }

            if(now!=10) bs = -1;
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
            rL += ","+jsonObject.get("Thema").getAsString();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return rL;
    }
}