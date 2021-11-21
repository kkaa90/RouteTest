package com.e.routetest;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
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


public class HomeFragment extends Fragment {

    ArrayList<Board> boards = new ArrayList<Board>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.home_board);
        BoardAdapter boardAdapter = new BoardAdapter(getActivity().getApplicationContext(),boards);

        //------------------------------ 여행지 알림 버튼 연결부 (시작) ------------------------------

        //임시데이터 생성
        ArrayList<PlaceWeatherTimeBasedata> placeWeatherTimeBasedataList = new ArrayList<>();

        placeWeatherTimeBasedataList.clear();
        placeWeatherTimeBasedataList.add(new PlaceWeatherTimeBasedata("동아대","부산시 사하구",35.11637001672873,128.9682497981559,"0900"));
        placeWeatherTimeBasedataList.add(new PlaceWeatherTimeBasedata("하단역","부산광역시 사하구",35.10630701217876,128.96670639796537,"1300"));
        placeWeatherTimeBasedataList.add(new PlaceWeatherTimeBasedata("구포시장","부산시 북구",35.20956456649422,129.00355907077622,"1715"));
        Button test_button = (Button)view.findViewById(R.id.show_trip_alarm_button);

        String tripAlarmTestData = new TripAlarmComponent().encoude_Route(placeWeatherTimeBasedataList);
        Log.d("HOMEFRAGMENTS DATA",tripAlarmTestData);

        test_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),TripAlarmListActivity.class);
                intent.putExtra("ROUTE_DATA",tripAlarmTestData);
                startActivity(intent);
            }
        });
        //------------------------------ 여행지 알림 버튼 연결부 (종료) ------------------------------

        new Thread(){
            public void run(){
                getSpot();
            }
        }.start();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boardAdapter.notifyDataSetChanged();
            }
        },2000);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(boardAdapter);
        Button testButton = (Button)view.findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),MyMenuActivity.class);
                startActivity(intent);

            }
        });


        return view;
    }
    public void getSpot() {
        BoardAdapter boardAdapter = new BoardAdapter(getActivity().getApplicationContext(),boards);
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

        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
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