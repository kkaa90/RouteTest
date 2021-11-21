package com.e.routetest;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TRoute {

    @PrimaryKey(autoGenerate = true)
    private int priamry_Key=0;
    private String placeNames;
    private String placeIDs;
    private String longitudes;
    private String latitudes;
    private String placeAddresses;
    private String arrivalTimes;
    private String isVisit;
    private String serverID;

    public TRoute(String placeNames, String placeIDs, String longitudes, String latitudes, String placeAddresses, String arrivalTimes, String isVisit, String serverID) {
        this.placeNames = placeNames;
        this.placeIDs = placeIDs;
        this.longitudes = longitudes;
        this.latitudes = latitudes;
        this.placeAddresses = placeAddresses;
        this.arrivalTimes = arrivalTimes;
        this.isVisit = isVisit;
        this.serverID = serverID;
    }

    public int getPriamry_Key() {return priamry_Key;}
    public void setPriamry_Key(int priamry_Key) {this.priamry_Key = priamry_Key;}

    public String getPlaceNames() {return placeNames;}
    public void setPlaceNames(String placeNames) {this.placeNames = placeNames;}

    public String getPlaceIDs() {return placeIDs;}
    public void setPlaceIDs(String placeIDs) {this.placeIDs = placeIDs;}

    public String getLongitudes() {return longitudes;}
    public void setLongitudes(String longitudes) {this.longitudes = longitudes;}

    public String getLatitudes() {return latitudes;}
    public void setLatitudes(String latitudes) {this.latitudes = latitudes;}

    public String getPlaceAddresses() {return placeAddresses;}
    public void setPlaceAddresses(String placeAddresses) {this.placeAddresses = placeAddresses;}

    public String getArrivalTimes() {return arrivalTimes;}
    public void setArrivalTimes(String arrivalTimes) {this.arrivalTimes = arrivalTimes;}

    public String getIsVisit() {return isVisit;}
    public void setIsVisit(String isVisit) {this.isVisit = isVisit;}

    public String getServerID() {return serverID;}
    public void setServerID(String serverID) {this.serverID = serverID;}
}
