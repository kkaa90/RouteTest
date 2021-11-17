package com.e.routetest;

//APIWorker 내부에 사용되는 데이터 클래스
public class trip_alarm_info {
    private String placeName, placeAddress, basedate, basetime, xpos, ypos, aLatitude, aLongitude, bLatitude, bLongitude;

    public trip_alarm_info(String placeName, String placeAddress,
                           String basedate, String basetime,
                           String xpos, String ypos,
                           String aLatitude, String aLongitude,
                           String bLatitude, String bLongitude){
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.basedate = basedate;
        this.basetime = basetime;
        this.xpos = xpos;
        this.ypos = ypos;
        this.aLatitude = aLatitude;
        this.aLongitude = aLongitude;
        this.bLatitude = bLatitude;
        this.bLongitude = bLongitude;
    }

    public String getPlaceAddress() {return placeAddress;}
    public void setPlaceAddress(String placeAddress) {this.placeAddress = placeAddress;}

    public String getPlaceName() {return placeName;}
    public void setPlaceName(String placeName) {this.placeName = placeName;}

    public String getBasedate() {return basedate;}
    public void setBasedate(String basedate) {this.basedate = basedate;}

    public String getBasetime() {return basetime;}
    public void setBasetime(String basetime) {this.basetime = basetime;}

    public String getXpos() {return xpos;}
    public void setXpos(String xpos) {this.xpos = xpos;}

    public String getYpos() {return ypos;}
    public void setYpos(String ypos) {this.ypos = ypos;}

    public String getaLatitude() {return aLatitude;}
    public void setaLatitude(String aLatitude) {this.aLatitude = aLatitude;}

    public String getaLongitude() {return aLongitude;}
    public void setaLongitude(String aLongitude) {this.aLongitude = aLongitude;}

    public String getbLatitude() {return bLatitude;}
    public void setbLatitude(String bLatitude) {this.bLatitude = bLatitude;}

    public String getbLongitude() {return bLongitude;}
    public void setbLongitude(String bLongitude) {this.bLongitude = bLongitude;}
}
