package com.e.routetest;

public class ReviewData {
    private int spotId;
    private String spotName;
    private float score = 0.0f;


    public ReviewData(int spotId, String spotName, float score) {
        this.spotId = spotId;
        this.spotName = spotName;
        this.score = score;
    }

    public int getSpotId() {
        return spotId;
    }

    public void setSpotId(int spotId) {
        this.spotId = spotId;
    }

    public String getSpotName() {
        return spotName;
    }

    public void setSpotName(String spotName) {
        this.spotName = spotName;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}