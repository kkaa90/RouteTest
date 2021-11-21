package com.e.routetest;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SpRepository {

    @Query("SELECT * FROM sp")
    List<Sp> findAll();

    @Query("SELECT * FROM sp where routeid= (:routeId)")
    Sp findById(int routeId);

    @Insert
    void insert(Sp sp);

    @Delete
    void delete(Sp sp);

    @Query("UPDATE sp SET svId=(:sId),date=(:sDate) where routeId=(:routeID)")
    void update(int sId, String sDate, int routeID);

}
