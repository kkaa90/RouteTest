package com.e.routetest;

import java.util.ArrayList;

public class Route {
    int routeId;
    String userId;
    String routeDate;
    String spots;
    String theme;
    String departureTime;
    String arrivalTime;

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRouteDate() {
        return routeDate;
    }

    public void setRouteDate(String routeDate) {
        this.routeDate = routeDate;
    }

    public String getSpots() {
        return spots;
    }

    public void setSpots(String spots) {
        this.spots = spots;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public Route(int routeId, String userId, String routeDate, String spots, String theme, String departureTime, String arrivalTime) {
        this.routeId = routeId;
        this.userId = userId;
        this.routeDate = routeDate;
        this.spots = spots;
        this.theme = theme;
        this.departureTime=departureTime;
        this.arrivalTime = arrivalTime;
    }
}
