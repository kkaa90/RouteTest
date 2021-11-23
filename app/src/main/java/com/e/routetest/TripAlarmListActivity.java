package com.e.routetest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.e.routetest.Workers.APIWorker;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.e.routetest.LoadingActivity.allSpotList;

public class TripAlarmListActivity extends AppCompatActivity {
    private static ArrayList<TripAlarm_rv_item_info> tripAlarmItems = new ArrayList<TripAlarm_rv_item_info>(); //날씨및 시간 정보(결과물)
    private static ArrayList<Boolean> isVisitList = new ArrayList<>();  //여행지 도착확인용
    private TripAlarmComponent component = new TripAlarmComponent();

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView refreshTime;
    private RecyclerView recyclerView;
    private TripAlarmListAdapter tripAlarmListAdapter;
    Spot s;
    Spot s2;
    public static Context tContext;
    public static boolean check;
    //뒤로가기 누를시 MainActivity로 이동
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_alarm_list);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.trip_alarm_activity_swipe_layout);
        refreshTime = (TextView)findViewById(R.id.trip_alarm_activity_refreshTime);
        recyclerView = (RecyclerView)findViewById(R.id.weatherList);
        tripAlarmListAdapter = new TripAlarmListAdapter(getApplicationContext(),tripAlarmItems,isVisitList);
        tContext=this;
        //실행시 데이터 받기 + 화면에 띄우기
        showItems();

        //새로고침
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //새로고침시 실행할 코드
                showItems();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //============================== Worker작동 시작 ==============================
        //WorkManager생성
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        //고유작업 worker작업요청 생성(10분주기 반복)
        PeriodicWorkRequest sendAPIWorkRequest = new
                PeriodicWorkRequest.Builder(APIWorker.class,10,TimeUnit.MINUTES).build();
        //workManager를 통해서 request를 큐에 올리기
        workManager.enqueueUniquePeriodicWork("GET_API_INFO", ExistingPeriodicWorkPolicy.KEEP,sendAPIWorkRequest);
        //취소하려면...
        //workManager.cancelUniqueWork("GET_API_INFO");
        //============================== Worker작동 종료 ==============================

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(tripAlarmListAdapter);
    }

    //======================================== 내부용 함수 시작 ========================================
    //API에서 정보를 받아오는 메서드
    private void showItems(){
        new Thread() {
            public void run() {
                //임시경로DB에서 데이터 받기
                tripAlarmItems.clear();
                component = new TripAlarmComponent();

                //DB연결
                TRouteDataBase tDb = TRouteDataBase.getInstance(getApplicationContext());
                //DB정보 받아오기
                List<TRoute> tRouteList = new ArrayList<>(tDb.tRouteRepository().findAll());

                //기상+날씨정보 초기화 및 정보 받기
                tripAlarmItems.clear(); tripAlarmItems.addAll(component.convert_LTRouteToALWeatherInfo(tRouteList,false));
                //Log.d("SHOWITEMS","tripAlarmItems size : "+tripAlarmItems.size());
                //방문정보내역 초기화 및 정보 받기
                isVisitList.clear(); isVisitList.addAll(component.extract_isVisitFromLTroute(tRouteList));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Log.d("SHOWITEMS","갱신시작");
                        tripAlarmListAdapter.notifyDataSetChanged();
                        //갱신일자 넣기
                        refreshTime.setText("갱신시간 : "+component.getNowTime());
                    }
                });
            }
        }.start();
    }
    protected void onActivityResult(int requestCode, int result, Intent data){
        super.onActivityResult(requestCode,result,data);
        if(requestCode==10){
            if(result==RESULT_OK){
                int now = data.getIntExtra("now",0);
                int spotId = data.getIntExtra("spotId",0);
                System.out.println("ID : "+spotId);
                System.out.println("now : "+ now);
                for(Spot obj:allSpotList){
                    if(obj.spotID==spotId){
                        s=obj;
                    }
                }
                for(Spot obj:allSpotList){
                    if(obj.spotID==Integer.parseInt(tripAlarmItems.get(now-1).getPlaceID())){
                        s2=obj;
                    }
                }
                final int[] t = new int[1];
                new Thread(){
                    public void run(){
                        t[0] = getTime(s2,s);
                    }
                }.start();

                while (t[0]==0){

                }
                component.updateTRouteByIndex(tContext,now,s, t[0]);
                while (!check){

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showItems();
                        check=false;
                    }
                });

            }
            else{

            }
        }
    }
    public int getTime(Spot A, Spot B){
        int arrtime=-1;
        double ax=A.spotX;
        double ay=A.spotY;
        double bx=B.spotX;
        double by=B.spotY;

        /*System.out.println("ax : "+ax);
        System.out.println("ay : "+ay);
        System.out.println("bx : "+bx);
        System.out.println("by : "+by);*/
        try {
            String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&mode=transit&origins="
                    +ax+","+ay+"&destinations="+bx+","+by+"&region=KR&key=AIzaSyAn-Tk1TUWDZWTHbdshVkc9z2uQG4dULNQ";
            //String url = "http://3.34.178.98:8080/teamproject/viewAttaction.jsp";
            System.out.println(url);


            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(url).get();
            Request request = builder.build();

            Response response = client.newCall(request).execute();
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(response.body().string());
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            JsonArray row = jsonObject.get("rows").getAsJsonArray();
            JsonObject jsonObject1 = (JsonObject) row.get(0);
            JsonArray row2 = jsonObject1.get("elements").getAsJsonArray();
            JsonObject jsonObject2 = (JsonObject) row2.get(0);
            JsonObject jsonObject3 = jsonObject2.get("duration").getAsJsonObject();
            arrtime = jsonObject3.get("value").getAsInt();

            System.out.println(arrtime);

        }catch (Exception e){
            e.printStackTrace();
        }
        return arrtime;
    }
}