package com.e.routetest;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Sp {
    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getSpotsName() {
        return spotsName;
    }

    public void setSpotsName(String spotsName) {
        this.spotsName = spotsName;
    }

    public String getSpotsX() {
        return spotsX;
    }

    public void setSpotsX(String spotsX) {
        this.spotsX = spotsX;
    }

    public String getSpotsY() {
        return spotsY;
    }

    public void setSpotsY(String spotsY) {
        this.spotsY = spotsY;
    }

    public String getSpotsAddress() {
        return spotsAddress;
    }

    public void setSpotsAddress(String spotsAddress) {
        this.spotsAddress = spotsAddress;
    }

    public String getSpotsId() {
        return spotsId;
    }

    public void setSpotsId(String spotsId) {
        this.spotsId = spotsId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getArrTime() {
        return arrTime;
    }

    public void setArrTime(String arrTime) {
        this.arrTime = arrTime;
    }

    public String getDesTime() {
        return desTime;
    }

    public void setDesTime(String desTime) {
        this.desTime = desTime;
    }

    public int getSvId() {
        return svId;
    }

    public void setSvId(int svId) {
        this.svId = svId;
    }

    public Sp(String spotsName, String spotsId, String spotsX, String spotsY, String spotsAddress, String date, String arrTime, String desTime, int svId) {
        this.spotsName = spotsName;
        this.spotsId = spotsId;
        this.spotsX = spotsX;
        this.spotsY = spotsY;
        this.spotsAddress = spotsAddress;
        this.date = date;
        this.arrTime = arrTime;
        this.desTime = desTime;
        this.svId = svId;
    }

    @PrimaryKey(autoGenerate = true)
    private int routeId=0;
    private String spotsName;
    private String spotsId;
    private String spotsX;
    private String spotsY;
    private String spotsAddress;
    private String date;
    private String arrTime;
    private String desTime;
    private int svId;
}
