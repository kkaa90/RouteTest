package com.e.routetest;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TRouteRepository {

    //전체 불러오기
    @Query("SELECT * FROM troute")
    List<TRoute> findAll();

    //삽입
    @Insert
    void insert(TRoute tRoute);

    //전체삭제
    @Query("DELETE FROM troute")
    void deleteAll();

    //삭제
    @Delete
    void delete(TRoute troute);

}
