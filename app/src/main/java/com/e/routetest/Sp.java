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

    public Sp(String spotsName, String spotsX, String spotsY, String spotsAddress) {
        this.spotsName = spotsName;
        this.spotsX = spotsX;
        this.spotsY = spotsY;
        this.spotsAddress = spotsAddress;
    }

    @PrimaryKey(autoGenerate = true)
    private int routeId;
    private String spotsName;
    private String spotsX;
    private String spotsY;
    private String spotsAddress;
}
