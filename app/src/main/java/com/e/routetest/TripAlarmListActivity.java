package com.e.routetest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TripAlarmListActivity extends AppCompatActivity {
    //기상청API Key
    private static final String API_KEY = "HEyK4no6dIj6pEs6Qw3Q%2FXnwiyH8MNJcMtcNplODjukvU1f8xdPMo7K2pVcATuJgx1%2BhD8aPCUofv617XqtqOw%3D%3D";

    /*
    //PlaceWeatherTimeBasedata로 통합
    private static ArrayList<String> placeNames = new ArrayList<>();        // 장소 이름 리스트
    private static ArrayList<String> placeAddresses = new ArrayList<>();    // 장소 주소 리스트
    private static ArrayList<Double> placeMapXList = new ArrayList<>();     // 장소 x좌표 리스트
    private static ArrayList<Double> placeMapYList = new ArrayList<>();     // 장소 y좌표 리스트
    */
    private static ArrayList<PlaceWeatherTimeBasedata> placeWeatherTimeBasedataList = new ArrayList<>();    //기본 정보

    private static ArrayList<TripAlarm_rv_item_info> tripAlarmItems = new ArrayList<>(); //여행 알람 정보 리스트

    //미사용 목록
    private static ArrayList<String> ArrivalTimeList = new ArrayList<>();   // 도착시간 리스트


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_alarm_list);

        /*
        //임시정보 넣기 시작(PlaceWeatherTimeBasedata로 통합
        placeNames.clear(); placeAddresses.clear(); placeMapXList.clear(); placeMapYList.clear();
        placeNames.add("동아대"); placeAddresses.add("사하구"); placeMapXList.add(35.11637001672873); placeMapYList.add(128.9682497981559);
        placeNames.add("하단역"); placeAddresses.add("사하구"); placeMapXList.add(35.10630701217876); placeMapYList.add(128.96670639796537);
        placeNames.add("구포시장"); placeAddresses.add("북구"); placeMapXList.add(35.20956456649422); placeMapYList.add(129.00355907077622);
        //임시정보 넣기 끝
         */
        placeWeatherTimeBasedataList.clear();
        placeWeatherTimeBasedataList.add(new PlaceWeatherTimeBasedata("동아대","사하구",35.11637001672873,128.9682497981559,"없음"));
        placeWeatherTimeBasedataList.add(new PlaceWeatherTimeBasedata("하단역","사하구",35.10630701217876,128.96670639796537,"없음"));
        placeWeatherTimeBasedataList.add(new PlaceWeatherTimeBasedata("구포시장","북구",35.20956456649422,129.00355907077622,"없음"));

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.weatherList);
        TripAlarmListAdapter tripAlarmListAdapter = new TripAlarmListAdapter(getApplicationContext(),tripAlarmItems);

        new Thread() {
            public void run() {
                /*
                //PlaceWeatherTimeBasedata통합이전
                weatherItems.clear();
                double tmpMapX, tmpMapY;
                for (int i = 0; i < placeNames.size(); i++) {
                    if((i+1) < placeNames.size()) { //다음 행선지가 있는 경우
                        tmpMapX = placeMapXList.get(i + 1); tmpMapY = placeMapYList.get(i+1);
                    }else{  //마지막 행선지인 경우
                        tmpMapX = 1000; tmpMapY = 1000;
                    }
                    Weather_rv_itemInfo weather_rv_itemInfo = getWeatherInfo(placeNames.get(i), placeAddresses.get(i), placeMapXList.get(i), placeMapYList.get(i), tmpMapX, tmpMapY);
                    weatherItems.add(weather_rv_itemInfo);
                 */
                //통합이후
                tripAlarmItems.clear();
                double nextLatitude, nextLongitude;    //경도, 위도
                for (int i = 0; i < placeWeatherTimeBasedataList.size(); i++) {
                    if((i+1) < placeWeatherTimeBasedataList.size()) { //다음 행선지가 있는 경우
                        nextLatitude = placeWeatherTimeBasedataList.get(i + 1).getLatitude();
                        nextLongitude = placeWeatherTimeBasedataList.get(i+1).getLongitude();
                    }else{  //마지막 행선지인 경우
                        nextLatitude = 1000; nextLongitude = 1000;
                    }
                    TripAlarm_rv_item_info tripAlarm_rv_item_info = getItemInfo(
                            placeWeatherTimeBasedataList.get(i).getPlaceName(),
                            placeWeatherTimeBasedataList.get(i).getPlaceAddress(),
                            placeWeatherTimeBasedataList.get(i).getLatitude(),
                            placeWeatherTimeBasedataList.get(i).getLongitude(),
                            nextLatitude, nextLongitude);
                    tripAlarmItems.add(tripAlarm_rv_item_info);
                }
            }
        }.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tripAlarmListAdapter.notifyDataSetChanged();
            }
        }, 3000);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(tripAlarmListAdapter);
    }

    //API에서 정보를 받아오는 메서드
    @NotNull
    private TripAlarm_rv_item_info getItemInfo(String placeName, String address, double placeLatitude, double placeLongitude, double nextLatitude, double nextLongitude){
        String API_KEY = "HEyK4no6dIj6pEs6Qw3Q%2FXnwiyH8MNJcMtcNplODjukvU1f8xdPMo7K2pVcATuJgx1%2BhD8aPCUofv617XqtqOw%3D%3D";

        //시간 관련 변수
        long mNow = System.currentTimeMillis();
        Date mReDate = new Date(mNow);
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
        SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
        String year_ = yearFormat.format(mReDate);
        String month_ = monthFormat.format(mReDate);
        String day_ = dayFormat.format(mReDate);
        String hour_ = hourFormat.format(mReDate);
        String minute_ = minuteFormat.format(mReDate);


        //------------------------------ 날씨정보 가져오기 시작 ------------------------------
        /*
        //TEST URL
        String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey="+API_KEY
                +"&numOfRows=14&pageNo=1&dataType=JSON&base_date="+"20211107"//year+month+day
                +"&base_time="+"2300"
                +"&nx="+conv_xpos(address)
                +"&ny="+conv_ypos(address);
        */
        //실제 URL
        String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey="+API_KEY
                +"&numOfRows=14&pageNo=1&dataType=JSON&base_date="+getBaseDate(year_, month_, day_, hour_, minute_)//year+month+day
                +"&base_time="+getBaseTime(hour_, minute_)
                +"&nx="+conv_xpos(address)
                +"&ny="+conv_ypos(address);

        Log.d("URL",url);

        String[] tempValues = new String[14];   //정보 임시 저장소

        try {
            OkHttpClient client = new OkHttpClient();

            Request.Builder builder = new Request.Builder().url(url).get();
            Request request = builder.build();


            Response response = client.newCall(request).execute();
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(response.body().string());
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonObject jsonObject1 = jsonObject.get("response").getAsJsonObject();
            JsonObject jsonObject2 = jsonObject1.get("body").getAsJsonObject();
            JsonObject jsonObject3 = jsonObject2.get("items").getAsJsonObject();
            JsonArray jsonArray = jsonObject3.get("item").getAsJsonArray();

            JsonObject tempjsonObject = jsonArray.get(0).getAsJsonObject();
            String targetTime = tempjsonObject.get("fcstTime").getAsString();

            JsonObject jsonObject4 = new JsonObject();
            for(int i=0;i<jsonArray.size();i++){
                jsonObject4 = jsonArray.get(i).getAsJsonObject();
                //시간대가 다른 카테고리 정보는 제외
                String tempTime = jsonObject4.get("fcstTime").getAsString();
                if(!targetTime.equals(tempTime))
                    break;

                String tempStr = jsonObject4.get("fcstValue").getAsString();
                int index_num = dataMapping(jsonObject4.get("category").getAsString());

                tempValues[index_num] = tempStr;

                Log.d("API_INFO",tempValues[index_num] +","+ index_num);
            }
        }
        catch(Exception e){
            for(int i=0;i<14;i++){
                tempValues[i]="null";
            }
            e.printStackTrace();
        }

        //날씨정보정제
        boolean isRain = true;
        int iconType = -1;
        String rainSnowInfo = " ";

        //날씨 SKY(5), PTY(1) = iconType
        switch(tempValues[1]){
            //강수형태 없음
            case "0":{
                switch(tempValues[5]){
                    case "1": iconType = 0; break;  //맑음
                    case "3": iconType = 1; break;  //구름많음
                    case "4": iconType = 2; break;  //흐림
                }
                break;
            }
            //빗방울, 비
            case "5":
            case "1": iconType = 3; break;
            //빗방울/눈날림, 비/눈(진눈깨비)
            case "6":
            case "2": isRain = false; iconType = 4; break;
            //눈날림, 눈
            case "7":
            case "3": isRain = false; iconType = 5; break;
            //소나기
            case "4": iconType = 6; break;
            default: iconType = 1;
        }

        if(isRain){ //비
            if(tempValues[2]!=null)
                rainSnowInfo = "강수량 : " + tempValues[2];
            else
                rainSnowInfo = "강수량 : 0mm";
        }
        else{
            if(tempValues[4]!=null)
                rainSnowInfo = "적설량 : " + tempValues[4];
            else
                rainSnowInfo = "적설량 : 0cm";
        }
        //------------------------------ 날씨정보 가져오기 종료 ------------------------------

        //------------------------------ 시간정보 가져오기 시작 ------------------------------
        double aLatitude = placeLatitude;
        double aLongitude = placeLongitude;
        double bLatitude = nextLatitude;
        double bLongitude = nextLongitude;

        int spendTime; //소요시간 저장
        String spendTime_text; //소요시간(String)

        Log.d("nowMap",":"+aLatitude+","+aLongitude);
        Log.d("nextMap",":"+bLatitude+","+bLongitude);

        if(nextLatitude>999||nextLongitude>999) {   //다음 행선지가 없는 경우
            spendTime = 0;
            spendTime_text = "없음";
            Log.d("LastDest","OK");
        }else{
            try{
                String time_url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&mode=transit&origins="
                        +aLatitude+","+aLongitude+"&destinations="+bLatitude+","+bLongitude+"&region=KR&key=AIzaSyAn-Tk1TUWDZWTHbdshVkc9z2uQG4dULNQ";
                Log.d("TIMEURL",time_url);

                OkHttpClient client = new OkHttpClient();
                Request.Builder builder = new Request.Builder().url(time_url).get();
                Request request = builder.build();

                Response response = client.newCall(request).execute();
                Gson gson = new Gson();
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonElement = jsonParser.parse(response.body().string());
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                JsonArray row = jsonObject.get("rows").getAsJsonArray();
                JsonObject jsonObject1 = (JsonObject) row.get(0);
                JsonArray row2 = jsonObject1.get("elements").getAsJsonArray();
                JsonObject jsonObject2 = (JsonObject) row2.get(0);
                JsonObject jsonObject3 = jsonObject2.get("duration").getAsJsonObject();
                spendTime = jsonObject3.get("value").getAsInt();
                spendTime_text = jsonObject3.get("text").getAsString();

                Log.d("API_SPENDTIME","0"+spendTime);
                Log.d("SPI_SPENDTIMEDETAIL",spendTime_text);
            }catch(Exception e){
                spendTime = -1;
                spendTime_text = "null";
                e.printStackTrace();
            }
        }

        //여행시간 정보 정제

        //------------------------------ 시간정보 가져오기 종료 ------------------------------

        //객체 생성
        TripAlarm_rv_item_info tempItem = new TripAlarm_rv_item_info(placeName, address, tempValues[6], tempValues[3], tempValues[0], rainSnowInfo, iconType, spendTime_text);


        //경고 알림 푸시
        //날씨 (비, 눈)
        //온도 (33이상, -10이하)
        //B 도착시간 - A 도착시간 <= 소요시간 + @일때 30분 / 20분 / 10분 단위 알림

        return tempItem;
    }
    //======================================== 내부용 함수 시작 ========================================
    //주소 -> 자체 격자 좌표 변환
    private String conv_xpos(String address){
        switch(address){
            case"중구": return "97";
            case"서구": return "97";
            case"동구": return "98";
            case"영도구": return "98";
            case"부산진구": return "97";

            case"동래구": return "98";
            case"남구": return "98";
            case"북구": return "96";
            case"해운대구": return "99";
            case"사하구": return "96";

            case"금정구": return "98";
            case"강서구": return "96";
            case"연제구": return "98";
            case"수영구": return "99";
            case"사상구": return "96";

            case"기장군": return "100";

            default: return "98"; //부산시
        }
    }

    private String conv_ypos(String address){
        switch(address){
            case"중구": return "74";
            case"서구": return "74";
            case"동구": return "75";
            case"영도구": return "74";
            case"부산진구": return "75";

            case"동래구": return "76";
            case"남구": return "75";
            case"북구": return "76";
            case"해운대구": return "75";
            case"사하구": return "74";

            case"금정구": return "77";
            case"강서구": return "76";
            case"연제구": return "76";
            case"수영구": return "75";
            case"사상구": return "75";

            case"기장군": return "77";

            default: return "76";   //부산시
        }
    }

    //basedate계산 (날짜변환) (getWeatherInfo에 사용됨)
    private String getBaseDate(String year_, String month_, String day_, String hour_, String minute_){
        int year = Integer.parseInt(year_);
        int month = Integer.parseInt(month_);
        int day = Integer.parseInt(day_);
        int hour = Integer.parseInt(hour_);
        int minute = Integer.parseInt(minute_);

        String tempY,tempM,tempD;

        //00:00~2:09 -> day-1
        if(hour>=0&&(hour<=2&&minute<=9)){
            day = day-1;
        }
        if(day<=0){
            switch(month){
                case 1:
                    day = 31; month = 12; year = year-1; break;
                case 3:
                    day = (((year%4==0)&&(year%100!=0))||(year%400==0))?29:28;
                    month = 2;
                    break;
                case 2: case 4: case 6: case 9: case 11:
                    day = 31; month = month-1; break;
                case 5: case 7: case 8: case 10: case 12:
                    day = 30; month = month-1; break;
            }
        }


        tempD = (day<10)?"0"+day : String.valueOf(day);
        tempM = (month<10)? "0"+month : String.valueOf(month);
        tempY = String.valueOf(year);

        return tempY+tempM+tempD;
    }

    //basetime계산 (시간변환) (getWeatherInfo에 사용됨)
    private String getBaseTime(String hour_, String minute_){
        int hour = Integer.parseInt(hour_);
        int minute = Integer.parseInt(minute_);
        String result = null;

        //2:10~5:09 -> 0200
        //5:10~8:09 -> 0500
        //8:10~11:09 -> 0800
        //11:10~14:09 -> 1100
        //14:10~17:09 -> 1400
        //17:10~20:09 -> 1700
        //20:10~23:09 -> 2000
        //23:10 ~ 2:09 -> 2300
        switch(hour){
            case 0: case 1: result = "2300"; break;
            case 2: result = (minute<=9)?"2300":"0200"; break;
            case 3: case 4: result = "0200"; break;
            case 5: result = (minute<=9)?"0200":"0500"; break;
            case 6: case 7: result = "0500"; break;
            case 8: result = (minute<=9)?"0500":"0800"; break;
            case 9: case 10: result = "0800"; break;
            case 11: result = (minute<=9)?"0800":"1100"; break;
            case 12: case 13: result = "1100"; break;
            case 14: result = (minute<=9)?"1100":"1400"; break;
            case 15: case 16: result = "1400"; break;
            case 17: result = (minute<=9)?"1400":"1700"; break;
            case 18: case 19: result = "1700"; break;
            case 20: result = (minute<=9)?"1700":"2000"; break;
            case 21: case 22: result = "2000"; break;
            case 23: result = (minute<=9)?"2000":"2300"; break;
        }
        return result;
    }

    //category - fcstValue 매칭 (getWeatherInfo에 사용됨)
    private int dataMapping(String dataType){
        switch(dataType){
            case "POP": return 0;
            case "PTY": return 1;
            case "PCP": return 2;
            case "REH": return 3;
            case "SNO": return 4;
            case "SKY": return 5;
            case "TMP": return 6;
            case "TMN": return 7;
            case "TMX": return 8;
            case "UUU": return 9;
            case "VVV": return 10;
            case "WAV": return 11;
            case "VEC": return 12;
            case "WSD": return 13;
        }
        return -1;
    }
}