package com.e.routetest;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

@Database(entities = Notify.class, version = 1)
public abstract class NotifyAppDatabase extends RoomDatabase {
    private static NotifyAppDatabase INSTANCE2 = null;
    public abstract NotifyRepository notifyRepository();

    public static NotifyAppDatabase getInstance(Context context){
        if(INSTANCE2==null){
            INSTANCE2 = Room.databaseBuilder(context.getApplicationContext(),
                    NotifyAppDatabase.class,"notify.db").fallbackToDestructiveMigration().build();
        }
        return INSTANCE2;
    }

    public static void destroyInstance(){
        INSTANCE2=null;
    }
}
