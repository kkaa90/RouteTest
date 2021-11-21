package com.e.routetest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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

public class TripAlarmListActivity extends AppCompatActivity {
    private static ArrayList<PlaceWeatherTimeBasedata> placeWeatherTimeBasedataList = new ArrayList<>();    //여행지 기본 정보
    private static ArrayList<TripAlarm_rv_item_info> tripAlarmItems = new ArrayList<TripAlarm_rv_item_info>(); //날씨및 시간 정보(결과물)
    private static ArrayList<Boolean> isVisitList = new ArrayList<>();  //여행지 도착확인용
    private TripAlarmComponent component = new TripAlarmComponent();

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView refreshTime;
    private RecyclerView recyclerView;
    private TripAlarmListAdapter tripAlarmListAdapter;

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

        //HomeFragment로부터 데이터 받기
        //Intent intent = getIntent();
        //String sourceData = intent.getStringExtra("DATA_FROM_INTRO_TO_LIST");
        //String v_sourceDate = intent.getStringExtra("V_DATA_FROM_INTRO_TO_LIST");
        //Log.d("DATA_FROM_INTRO_TO_LIST",sourceData);
        //Log.d("V_DATA_FROM_INTRO_TO_LIST",v_sourceDate);
        placeWeatherTimeBasedataList.clear();
        //placeWeatherTimeBasedataList = component.decode_Route(sourceData);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.trip_alarm_activity_swipe_layout);
        refreshTime = (TextView)findViewById(R.id.trip_alarm_activity_refreshTime);
        recyclerView = (RecyclerView)findViewById(R.id.weatherList);
        tripAlarmListAdapter = new TripAlarmListAdapter(getApplicationContext(),tripAlarmItems,isVisitList);

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
    void showItems(){
        new Thread() {
            public void run() {
                //임시경로DB에서 데이터 받기
                TripAlarmComponent component = new TripAlarmComponent();

                TempPlaceDatabase tDb = TempPlaceDatabase.getInstance(getApplicationContext());
                List<TempPlace> tempRoutedata = new ArrayList<>(tDb.tempPlaceRepository().findAll());
                ArrayList<TripAlarm_rv_item_info> basedata = new ArrayList<>();
                ArrayList<Boolean> _isVisitList = new ArrayList<>();

                basedata.clear();
                _isVisitList.clear();
                if(tempRoutedata != null) {
                    for (int i = 0; i < tempRoutedata.size(); i++) {
                        _isVisitList.add(tempRoutedata.get(i).isVisit());
                        if (tempRoutedata.get(i).isVisit()) { //방문을 했던 곳은 스킵
                            continue;
                        } else {
                            //다음 행선지가 있는 경우 = 다음행선지 정보/ 없는경우 1000 or null
                            double bLatitude = (i + 1) < tempRoutedata.size() ? Double.parseDouble(tempRoutedata.get(i + 1).getLatitude()) : 1000;
                            double bLongitude = (i + 1) < tempRoutedata.size() ? Double.parseDouble(tempRoutedata.get(i + 1).getLongitude()) : 1000;
                            String nextArrivalTime = (i + 1) < tempRoutedata.size() ? tempRoutedata.get(i + 1).getArrivalTime() : "null";

                            basedata.add(component.getItemInfo(
                                    tempRoutedata.get(i).getPlaceName(),
                                    component.split_Str(tempRoutedata.get(i).getPlaceAddress()),
                                    Double.parseDouble(tempRoutedata.get(i).getLatitude()),
                                    Double.parseDouble(tempRoutedata.get(i).getLongitude()),
                                    bLatitude, bLongitude,
                                    tempRoutedata.get(i).getArrivalTime(), nextArrivalTime));
                        }
                    }
                }
                tripAlarmItems.addAll(basedata);
                isVisitList.addAll(_isVisitList);

                Log.d("SHOW_INFO","데이터받기 종료");
                /*
                tripAlarmItems.clear();
                double nextLatitude, nextLongitude;    //경도, 위도
                String nextArrivalTime;
                for (int i = 0; i < placeWeatherTimeBasedataList.size(); i++) {
                    //방문한적 있는 곳이면 자료갱신 안함
                    if(isVisitList.get(i)){
                        Log.d("LIST_API_STATUS","skip("+i+")");
                        continue;
                    }
                    Log.d("LIST_API_STATUS","OK("+i+")");
                    if((i+1) < placeWeatherTimeBasedataList.size()) { //다음 행선지가 있는 경우
                        nextLatitude = placeWeatherTimeBasedataList.get(i + 1).getLatitude();
                        nextLongitude = placeWeatherTimeBasedataList.get(i+1).getLongitude();
                        nextArrivalTime = placeWeatherTimeBasedataList.get(i+1).getArrivalTime();
                    }else{  //마지막 행선지인 경우
                        nextLatitude = 1000; nextLongitude = 1000;
                        nextArrivalTime = "null";
                    }
                    TripAlarm_rv_item_info tripAlarm_rv_item_info = component.getItemInfo(
                            placeWeatherTimeBasedataList.get(i).getPlaceName(),
                            placeWeatherTimeBasedataList.get(i).getPlaceAddress(),
                            placeWeatherTimeBasedataList.get(i).getLatitude(),
                            placeWeatherTimeBasedataList.get(i).getLongitude(),
                            nextLatitude, nextLongitude,
                            placeWeatherTimeBasedataList.get(i).getArrivalTime(),nextArrivalTime);
                    tripAlarmItems.add(tripAlarm_rv_item_info);
                }
                 */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tripAlarmListAdapter.notifyDataSetChanged();
                        //갱신일자 넣기
                        refreshTime.setText("갱신시간 : "+component.getNowTime());
                    }
                });

            }
        }.start();
        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tripAlarmListAdapter.notifyDataSetChanged();
            }
        }, 3000);
        //갱신일자 넣기
        refreshTime.setText("갱신시간 : "+component.getNowTime());
         */
    }
}