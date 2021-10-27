package com.e.routetest;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Sp.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SpRepository spRepository();
}
