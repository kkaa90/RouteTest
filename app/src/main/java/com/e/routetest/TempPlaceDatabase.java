package com.e.routetest;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {TempPlace.class}, version = 2)
public abstract class TempPlaceDatabase extends RoomDatabase {
    private static TempPlaceDatabase INSTANCE3 = null;
    public abstract TempPlaceRepository tempPlaceRepository();

    public static TempPlaceDatabase getInstance(Context context){
        if(INSTANCE3 == null){
            INSTANCE3 = Room.databaseBuilder(context.getApplicationContext(),
                    TempPlaceDatabase.class,"tempPlace.db").fallbackToDestructiveMigration().build();
        }
        return INSTANCE3;
    }

    public static void destroyInstace() {INSTANCE3 = null;}
}
