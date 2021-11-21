package com.e.routetest;

import android.content.Context;

import androidx.room.Transaction;

import java.util.ArrayList;

public class TempPlaceDatabaseController {

    @Transaction
    public void updateData (ArrayList<TempPlace> basedata, int start, int finish, Context context) {
        TempPlaceDatabase tDb = TempPlaceDatabase.getInstance(context);
        new Thread() {
            public void run() {
                //내부에 entity가 있으면 업데이트 없으면 새로추가
                for(int i=start;i<=finish;i++){
                    if(tDb.tempPlaceRepository().findById(i+1)!=null){
                        tDb.tempPlaceRepository()
                                .updateAll(
                                        i,
                                        basedata.get(i).getPlaceID(),
                                        basedata.get(i).getPlaceName(),
                                        basedata.get(i).getPlaceAddress(),
                                        basedata.get(i).getLatitude(),
                                        basedata.get(i).getLongitude(),
                                        basedata.get(i).getArrivalTime());
                    }
                    else{
                        tDb.tempPlaceRepository()
                                .insert(basedata.get(i));
                    }
                }
            }
        }.start();
    }
}
