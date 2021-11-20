package com.e.routetest;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TempPlace {

    @PrimaryKey(autoGenerate = true)
    private int routeId = 0;
    private int placeIndex;     //장소 순서
    private String placeName;   //장소 이름
    private String placeAddress;
    private String latitude;
    private String longitude;
    private String arrivalTime;
    private boolean isVisit;

    public TempPlace(int placeIndex, String placeName, String placeAddress, String latitude, String longitude, String arrivalTime, boolean isVisit){
        this.placeIndex = placeIndex;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.arrivalTime = arrivalTime;
        this.isVisit = isVisit;
    }

    public int getPlaceIndex() {return placeIndex;}
    public void setPlaceIndex(int placeIndex) {this.placeIndex = placeIndex;}

    public int getRouteId() {return routeId;}
    public void setRouteId(int routeId) {this.routeId = routeId;}

    public String getPlaceName() {return placeName;}
    public void setPlaceName(String placeName) {this.placeName = placeName;}

    public String getPlaceAddress() {return placeAddress;}
    public void setPlaceAddress(String placeAddress) {this.placeAddress = placeAddress;}

    public String getLatitude() {return latitude;}
    public void setLatitude(String latitude) {this.latitude = latitude;}

    public String getLongitude() {return longitude;}
    public void setLongitude(String longitude) {this.longitude = longitude;}

    public String getArrivalTime() {return arrivalTime;}
    public void setArrivalTime(String arrivalTime) {this.arrivalTime = arrivalTime;}

    public boolean isVisit() {return isVisit;}
    public void setVisit(boolean visit) {isVisit = visit;}
}
