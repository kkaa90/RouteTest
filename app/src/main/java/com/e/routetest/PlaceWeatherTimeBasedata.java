package com.e.routetest;

public class PlaceWeatherTimeBasedata {
    private String placeName;
    private String placeAddress;
    private double latitude;
    private double longitude;
    private String arrivalTime;

    //getter,setter
    public String getPlaceName(){return this.placeName;}
    public void setPlaceName(String placeName) {this.placeName = placeName;}

    public String getPlaceAddress(){return this.placeAddress;}
    public void setPlaceAddress(String placeAddress){this.placeAddress = placeAddress;}

    public double getLatitude(){return this.latitude;}
    public void setLatitude(double latitude){this.latitude = latitude;}

    public double getLongitude(){return this.longitude;}
    public void setLongitude(double longitude){this.longitude = longitude;}

    public String getArrivalTime(){return this.arrivalTime;}
    public void setArrivalTime(String arrivalTime){this.arrivalTime = arrivalTime;}


    //생성자
    PlaceWeatherTimeBasedata(String placeName, String placeAddress, double latitude, double longitude, String arrivalTime){
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.arrivalTime = arrivalTime;
    }
}
