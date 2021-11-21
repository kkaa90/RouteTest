package com.e.routetest;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TempPlaceRepository {

    //primary Key로 찾기
    @Query("SELECT * FROM tempplace WHERE routeID = (:routeID)")
    TempPlace findById(int routeID);

    //임시경로 전체 불러오기
    @Query("SELECT * FROM tempplace")
    List<TempPlace> findAll();

    //추가용
    @Insert
    void insert(TempPlace tempPlace);

    //특정 routeID내부 요소 변경(
    @Query("UPDATE tempplace " +
            "SET placeID = (:placeID), placeIndex = (:placeIndex), placeName = (:placeName), placeAddress = (:placeAddress), latitude = (:latitude), longitude = (:longitude), arrivalTime = (:arrivalTime)" +
            "WHERE routeId = (:routeId) ")
    void updateAll(int routeId, int placeIndex, int placeID, String placeName, String placeAddress, String latitude, String longitude, String arrivalTime);

    //특정 entity의 isVisit변경
    @Query("UPDATE tempplace SET isVIsit = (:isVisit) WHERE placeIndex = (:placeIndex)")
    void updateIsVisit(int placeIndex, boolean isVisit);

    //임시경로 전체 삭제
    @Delete
    void delete(TempPlace tempPlace);
}
