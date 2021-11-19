package com.e.routetest;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotifyRepository {


    @Query("SELECT * FROM notify")
    List<Notify> findAll();

    @Query("SELECT * FROM sp where routeid= (:num)")
    Sp findById(int num);

    @Insert
    void insert(Notify notify);

    @Delete
    void delete(Notify notify);
}
