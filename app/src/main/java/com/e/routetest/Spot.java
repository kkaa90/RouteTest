package com.e.routetest;

public class Spot {
    int spotID;
    String spotName;
    double spotX;
    double spotY;
    String spotAddress;
   // float score;
    public int getSpotID() {
        return spotID;
    }

    public void setSpotID(int spotID) {
        this.spotID = spotID;
    }

    public String getSpotName() {
        return spotName;
    }

    public void setSpotName(String spotName) {
        this.spotName = spotName;
    }

    public double getSpotX() {
        return spotX;
    }

    public void setSpotX(double spotX) {
        this.spotX = spotX;
    }

    public double getSpotY() {
        return spotY;
    }

    public void setSpotY(double spotY) {
        this.spotY = spotY;
    }

    public String getSpotAddress() {
        return spotAddress;
    }

    public void setSpotAddress(String spotAddress) {
        this.spotAddress = spotAddress;
    }

//    public float getScore() {
//        return score;
//    }
//
//    public void setScore(float score) {
//        this.score = score;
//    }

    public Spot(int spotID, String spotName, double spotX, double spotY, String spotAddress){
        this.spotID=spotID;
        this.spotName=spotName;
        this.spotX=spotX;
        this.spotY=spotY;
        this.spotAddress=spotAddress;
       // this.score=score;
    }
}
