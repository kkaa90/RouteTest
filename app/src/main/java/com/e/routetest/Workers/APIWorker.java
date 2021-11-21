package com.e.routetest.Workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.e.routetest.NotifyAppDatabase;
import com.e.routetest.TRoute;
import com.e.routetest.TRouteDataBase;
import com.e.routetest.TripAlarmComponent;
import com.e.routetest.TripAlarm_rv_item_info;

import java.util.ArrayList;
import java.util.List;

public class APIWorker extends Worker {
    public APIWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        int TOTAL_ELEMENTS = 8;

        NotifyAppDatabase notifyDb = NotifyAppDatabase.getInstance(getApplicationContext());
        TripAlarmComponent component = new TripAlarmComponent();

        Log.d("APIWORKER_STATE","ACTIVATE");

        //임시경로DB에서 데이터 받기
        TRouteDataBase tDb = TRouteDataBase.getInstance(getApplicationContext());
        //임시경로 받기
        List<TRoute> tRouteList = new ArrayList<>(tDb.tRouteRepository().findAll());
        //날씨+시간정보 받을 곳 생성 및 정보 받기
        ArrayList<TripAlarm_rv_item_info> basedata = new ArrayList<>();
        basedata.clear();
        basedata.addAll(component.convert_LTRouteToALWeatherInfo(tRouteList,true));

        //특이사항 체크 및 푸시
        if (!basedata.isEmpty()) {
            for(int i=0;i<basedata.size();i++){
                component.tripAlarmPush(basedata.get(i),getApplicationContext());
            }
        }

        Log.d("APIWORKER_STATE","FINISH");
        return Result.success();
    }
}
