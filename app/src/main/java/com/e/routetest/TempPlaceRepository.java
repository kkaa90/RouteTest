package com.e.routetest;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TempPlaceRepository {

    //임시경로 전체 불러오기
    @Query("SELECT * FROM tempplace")
    List<TempPlace> findAll();

    //추가용
    @Insert
    void insert(TempPlace tempPlace);

    //임시경로 전체 삭제
    @Delete
    void delete(TempPlace tempPlace);

}
