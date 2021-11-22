package com.e.routetest;

import android.content.Context;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.e.routetest.LoadingActivity.allSpotList;
import static com.e.routetest.LoadingActivity.sv;


public class HomeFragment extends Fragment {

    ArrayList<Board> boards = new ArrayList<Board>();
    ArrayList<Sp> routes = new ArrayList<Sp>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.home_board);
        BoardAdapter boardAdapter = new BoardAdapter(getActivity().getApplicationContext(),boards);

        //Route 리사이클러뷰 생성 및 적용
        RecyclerView route_recyclerView = (RecyclerView)view.findViewById(R.id.home_route);
        HomeFragmentSpAdapter homeFragmentSpAdapter = new HomeFragmentSpAdapter(getActivity().getApplicationContext(),routes);

        route_recyclerView.setHasFixedSize(true);
        route_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(),RecyclerView.HORIZONTAL,false));
        route_recyclerView.setAdapter(homeFragmentSpAdapter);

        //Route에 필요한 데이터 받아온 뒤 notifyChanged실행

        //------------------------------ 여행지 알림 버튼 연결부 (시작) ------------------------------
        //임시데이터 생성
        Sp test_route = new Sp(
                "금정산 (부산 국가지질공원),광안리해수욕장,다대포해수욕장,송정해수욕장,해운대해수욕장",
                "126028,126078,126079,126080,126081",
                "129.0518587773,129.1184922375,128.9680332493,129.1996400523,129.1603078991",
                "35.2684489780,35.1537908369,35.0464195263,35.1787117732,35.1591243474",
                "부산광역시 금정구 금성동,부산광역시 수영구 광안해변로 219,부산광역시 사하구 몰운대1길 14,부산광역시 해운대구 송정해변로 62,부산광역시 해운대구 해운대해변로 264",
                "20211102",
                "32400,43200,51300,58500,69300",
                "36000,45000,56100,61200,75600",
                111);
        //routes.add(test_route);
        //String tripAlarmTestData = new TripAlarmComponent().convert_SptoString(test_route);
        //Log.d("HOMEFRAGMENTS DATA",tripAlarmTestData);

        /*
        Button test_button1 = (Button)view.findViewById(R.id.show_trip_alarm_button1);
        test_button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new TripAlarmComponent().save_Sp_on_TRouteDB(test_route,getContext());
                Intent intent = new Intent(getContext(),TripAlarmListActivity.class);
                //Intent intent = new Intent(getContext(),TripAlarmIntroActivity.class);
               // intent.putExtra("DATA_FROM_HOMEFRAGMENT_TO_INTROACTIVITY",tripAlarmTestData);
                startActivity(intent);
            }
        });
         */

        Button test_button2 = (Button)view.findViewById(R.id.show_trip_alarm_button2);
        test_button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),TripAlarmListActivity.class);
                startActivity(intent);
            }
        });
        //------------------------------ 여행지 알림 버튼 연결부 (종료) ------------------------------

        new Thread(){
            public void run(){
                getSpot();
                //오늘날짜 기반으로 routes데이터 업데이트
                routes.clear();
                routes.addAll(getRouteByDate(getToday(),getActivity().getApplicationContext()));
            }
        }.start();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                homeFragmentSpAdapter.notifyDataSetChanged();
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

    //날짜기반으로 Sp데이터 받아오기
    private List<Sp> getRouteByDate(String date, Context context){
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        List<Sp> spList = appDatabase.spRepository().findAllbyDate(date);

        return spList;
    }

    //오늘 날짜 받아오기 (yyyy-MM-dd)
    String getToday(){
        long mNow = System.currentTimeMillis();
        Date mReDate = new Date(mNow);
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        String year_ = yearFormat.format(mReDate);
        String month_ = monthFormat.format(mReDate);
        String day_ = dayFormat.format(mReDate);

        return year_+"-"+month_+"-"+day_;
    }
}