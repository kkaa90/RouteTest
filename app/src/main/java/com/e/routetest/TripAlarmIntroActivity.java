package com.e.routetest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.time.LocalTime;
import java.util.ArrayList;

import static com.google.firebase.database.core.RepoManager.clear;


//경로를 받아서 임시저장 데이터베이스로 보낸뒤 그 데이터를 TripAlarmListActivity로 전달하는 액티비티
//type 1 : 기존경로 사용 / type 2 : 새로운 경로 사용
public class TripAlarmIntroActivity extends AppCompatActivity {
    private TempPlaceRepository tempPlaceRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_alarm_intro);

        TripAlarmComponent component = new TripAlarmComponent();
        //ArrayList<PlaceWeatherTimeBasedata> basedata = new ArrayList<>();

        //HomeFragment로부터 데이터 넘겨받기기
       Intent intent = getIntent();
        String sourceData = intent.getStringExtra("DATA_FROM_HOMEFRAGMENT_TO_INTROACTIVITY");
        Log.d("DATA FROM HOMEFRAGMENT TO INTROACTIVITY",sourceData);
        //basedata.clear();
        //basedata = component.decode_Route(sourceData);

        //새로운 경로에서 데이터 추출및 TempPlace로 변환
        //String result = null;
        //String v_result = null;

        ArrayList<TempPlace>temp_route = new ArrayList<>();
        String[] temp = sourceData.split(",",2);
        int arraySize = Integer.parseInt(temp[0]);

        String placeName = null;
        int placeID = 0;
        String placeAddress = null;
        String latitude = null;
        String longitude = null;
        String arrivaTime = null;

        //result = Integer.toString(arraySize);
        //v_result = Integer.toString(arraySize);

        String temp2 = temp[1];
        String[] items = temp2.split(",");
        temp_route.clear();
        int index = 0;
        for(int i=0;i<items.length;i++){
            int j = i%6;        //elements 6개
            switch(j){
                case 0: placeName = items[i]; break;
                case 1: placeID = Integer.parseInt(items[i]); break;
                case 2: placeAddress = items[i]; break;
                case 3: latitude = items[i]; break;
                case 4: longitude = items[i]; break;
                case 5:
                    arrivaTime = items[i];
                    temp_route.add(new TempPlace(index, placeID, placeName, placeAddress, latitude, longitude, arrivaTime,false));
                    //result = result + "," + placeName + "," + placeAddress + "," + latitude + "," + longitude + "," + arrivaTime;
                    //v_result = v_result + "," + "0";
                    index++;
                    break;
            }
        }

        //임시경로 데이터베이스에 저장
        TempPlaceDatabase tDb = TempPlaceDatabase.getInstance(this);
        new TempPlaceDatabaseController().updateData(temp_route,0,temp_route.size()-1,getApplicationContext());
        /*
        for(int i=0;i<arraySize;i++){
            new InsertTempRouteAsyncTask(tDb.tempPlaceRepository()).execute(temp_route.get(i));
        }
         */

        intent = new Intent(getApplicationContext(),TripAlarmListActivity.class);
        //intent.putExtra("DATA_FROM_INTRO_TO_LIST",sourceData);  //경로 정보
        //intent.putExtra("V_DATA_FROM_INTRO_TO_LIST",v_result);  //방문 정보
        //Log.d("DATA_FROM_INTRO_TO_LIST",sourceData);
        startActivity(intent);

    }

    /*
    private class InsertTempRouteAsyncTask extends AsyncTask<TempPlace, Void, Void> {
        private TempPlaceRepository tempPlaceRepository;

        public InsertTempRouteAsyncTask(TempPlaceRepository tempPlaceRepository){
            this.tempPlaceRepository = tempPlaceRepository;
        }
        @Override
        protected Void doInBackground(TempPlace... tempPlaces){

            tempPlaceRepository.insert(tempPlaces[0]);
            return null;
        }
    }
     */
}