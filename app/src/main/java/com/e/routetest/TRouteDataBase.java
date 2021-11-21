package com.e.routetest;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {TRoute.class}, version = 1)
public abstract class TRouteDataBase extends RoomDatabase {
    private static TRouteDataBase INSTANCE3 = null;
    public abstract TRouteRepository tRouteRepository();

    public static TRouteDataBase getInstance(Context context){
        if(INSTANCE3 == null){
            INSTANCE3 = Room.databaseBuilder(context.getApplicationContext(),
                    TRouteDataBase.class,"tRoute.db").fallbackToDestructiveMigration().build();
        }
        return INSTANCE3;
    }
}
