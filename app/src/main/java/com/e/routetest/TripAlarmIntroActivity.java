package com.e.routetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import static com.google.firebase.database.core.RepoManager.clear;


//경로를 받아서 임시저장 데이터베이스로 보낸뒤 그 데이터를 TripAlarmListActivity로 전달하는 액티비티
//type 1 : 기존경로 사용 / type 2 : 새로운 경로 사용
public class TripAlarmIntroActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_alarm_intro);

        TripAlarmComponent component = new TripAlarmComponent();
        ArrayList<PlaceWeatherTimeBasedata> basedata = new ArrayList<>();

        //HomeFragment로부터 데이터 넘겨받기기
       Intent intent = getIntent();
        String sourceData = intent.getStringExtra("ROUTE_DATA");
        Log.d("DATA FROM HOMEFRAGMENT TO INTROACTIVITY",sourceData);
        basedata.clear();
        basedata = component.decode_Route(sourceData);

        //새로운 경로를 받아서 저장하는 경우

        //
        ArrayList<TempPlace>temp_route = new ArrayList<>();
        String[] temp = sourceData.split(",",2);
        int arraySize = Integer.parseInt(temp[0]);

        String placeName = null;
        String placeAddress = null;
        String latitude = null;
        String longitude = null;
        String arrivaTime = null;

        String[] items = temp[1].split(",");
        temp_route.clear();
        int index = 0;
        for(int i=0;i<arraySize;i++){
            int j = i%5;
            switch(j){
                case 0: placeName = items[j]; break;
                case 1: placeAddress = items[j]; break;
                case 2: latitude = items[j]; break;
                case 3: longitude = items[j]; break;
                case 4:
                    arrivaTime = items[j];
                    temp_route.add(new TempPlace(index,placeName,placeAddress,latitude,longitude,arrivaTime,false));
                    index++;
                    break;
            }
        }

        TempPlaceDatabase tDb = TempPlaceDatabase.getInstance(this);
        for(int i=0;i<arraySize;i++){
            //new MyFireBaseMessagingService.InsertAsyncTask(tDb.tempPlaceRepository()).execute()
        }





    }
}