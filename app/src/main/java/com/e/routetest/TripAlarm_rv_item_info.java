package com.e.routetest;

public class TripAlarm_rv_item_info {
    //날씨정보용 변수
    private String placeName;   //여행지이름
    private String address;     //주소
    private String placeTmp;    //온도
    private String placeHum;    //습도
    private String placeRainfallProb;   //강수확률
    private String placeRainfallInfo;   //강수량
    private String placeSnowInfo;       //적설량
    private int placeWeatherIconType;   //날씨아이콘 표시용
    //private boolean isVisit;    //도착했는지확인용

    //여행시간 관련 정보 변수
    private String ArrivalTime;
    private int spendingTime;    //소요시간 초단위
    private String spendingTime_text; //소요시간 분단위 텍스트

    //생성자
    public TripAlarm_rv_item_info(
            String placeName,
            String address,
            String placeTmp,
            String placeHum,
            String placeRainfallProb,
            String placeRainfallInfo,
            int placeWeatherIconType,
            String spendingTime_text){
        this.placeName = placeName;
        this.address = address;
        this.placeTmp = placeTmp;
        this.placeHum = placeHum;
        this.placeRainfallProb = placeRainfallProb;
        this.placeRainfallInfo = placeRainfallInfo;
        this.placeWeatherIconType = placeWeatherIconType;
        this.spendingTime_text = spendingTime_text;
        //this.isVisit = false;
    }

    //======================================== getter, setter 시작 ========================================
    public String getPlaceName(){return placeName;}
    public void setPlaceName(String placeName){this.placeName = placeName;}

    public String getAddress(){return address;}
    public void setAddress(String address){this.address = address;}

    public String getPlaceTmp(){return placeTmp;}
    public void setPlaceTmp(String placeTmp){this.placeTmp = placeTmp;}

    public String getPlaceHum(){return placeHum;}
    public void setPlaceHum(String placeHum){this.placeHum = placeHum;}

    public String getPlaceRainfallProb(){return placeRainfallProb;}
    public void setPlaceRainfallProb(String placeRainfallProb){this.placeRainfallProb = placeRainfallProb;}

    public String getPlaceRainfallInfo(){return placeRainfallInfo;}
    public void setPlaceRainfallInfo(String placeRainfallInfo){this.placeRainfallInfo = placeRainfallInfo;}

    public String getPlaceSnowInfo(){return placeSnowInfo;}
    public void setPlaceSnowInfo(String placeSnowInfo){this.placeSnowInfo = placeSnowInfo;}

    public int getPlaceWeatherIconType(){return placeWeatherIconType;}
    public void setPlaceWeatherIconType(int placeWeatherIconType){this.placeWeatherIconType = placeWeatherIconType;}

    public int getSpendingTime(){return spendingTime;}
    public void setSpendingTime(int spendingTime){this.spendingTime = spendingTime;}

    public String getSpendingTime_text(){return spendingTime_text;}
    public void setSpendingTime_text(String spendingTime_text){this.spendingTime_text = spendingTime_text;}

    //public boolean getIsVisit(){return isVisit;}
    //public void setIsVisit(boolean isVisit){this.isVisit = isVisit;}
    //======================================== getter, setter 끝 ========================================
}
