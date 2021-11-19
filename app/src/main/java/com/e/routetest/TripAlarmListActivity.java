package com.e.routetest;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.e.routetest.Workers.APIWorker;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TripAlarmListActivity extends AppCompatActivity {
    //기상청API Key
    private static ArrayList<PlaceWeatherTimeBasedata> placeWeatherTimeBasedataList = new ArrayList<>();    //기본 정보
    private static ArrayList<TripAlarm_rv_item_info> tripAlarmItems = new ArrayList<TripAlarm_rv_item_info>(); //여행 알람 정보 리스트 (LiveData)
    private static ArrayList<Boolean> isVisitList = new ArrayList<>();  //여행지 도착확인용

    private int total_index;    //전체 여행지 인덱스
    private int now_index;  //지금까지 도착한 여행지 인덱스

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView refreshTime;
    private RecyclerView recyclerView;
    private TripAlarmListAdapter tripAlarmListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_alarm_list);


        //여행지 목록 불러온뒤 인덱스 초기화
        int total_index = placeWeatherTimeBasedataList.size();
        now_index = 0;


        //임시데이터
        placeWeatherTimeBasedataList.clear();
        placeWeatherTimeBasedataList.add(new PlaceWeatherTimeBasedata("동아대","사하구",35.11637001672873,128.9682497981559,"없음"));
        placeWeatherTimeBasedataList.add(new PlaceWeatherTimeBasedata("하단역","사하구",35.10630701217876,128.96670639796537,"없음"));
        placeWeatherTimeBasedataList.add(new PlaceWeatherTimeBasedata("구포시장","북구",35.20956456649422,129.00355907077622,"없음"));
        //임시데이터 방문정보
        isVisitList.clear();
        for(int i=0;i<placeWeatherTimeBasedataList.size();i++){
            isVisitList.add(false);
        }


        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.trip_alarm_activity_swipe_layout);
        refreshTime = (TextView)findViewById(R.id.trip_alarm_activity_refreshTime);
        recyclerView = (RecyclerView)findViewById(R.id.weatherList);
        tripAlarmListAdapter = new TripAlarmListAdapter(getApplicationContext(),tripAlarmItems,isVisitList);


        //실행시 데이터 받기 + 화면에 띄우기
        showItems();
        //

        //새로고침
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //새로고침시 실행할 코드
                showItems();
            }
        });


        //============================== Worker작동 시작 ==============================
        String encoding_data = convert_placeInfo(placeWeatherTimeBasedataList);
        Log.d("APIWORKER_INPUTDATA",encoding_data);
        //Data객체를 생성해서 worker에 전달할 값 생성
        Data APIWorker_inputData = new Data.Builder().putString("APIWORKER_INPUTDATA",encoding_data).build();
        //WorkRequest를 통해서 작업요청 생성(15분주기 반복 실행)
        WorkRequest APIWorker_request = new PeriodicWorkRequest.Builder(APIWorker.class,15, TimeUnit.MINUTES).setInputData(APIWorker_inputData).build();
        //WorkManger를 통해서 작업요청을 큐에 올리기
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        workManager.enqueue(APIWorker_request);
        //============================== Worker작동 종료 ==============================


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(tripAlarmListAdapter);
    }



    //======================================== 내부용 함수 시작 ========================================
    //API에서 정보를 받아오는 메서드
    void showItems(){
        new Thread() {
            public void run() {
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
        //갱신일자 넣기
        refreshTime.setText("갱신시간 : "+getNowTime());
    }

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

    private String split_Str(String source){
        String[] tmp = source.split(" ",2);
        String tmp2 = tmp[1];
        String[] tmp3 = tmp2.split("구|군",2);
        String result =  tmp3[1];

        if(result.equals("기장")){
            result += "군";
        }
        else{
            result += "구";
        }

        return result;
    }

    /*
    @NotNull
    private boolean getItemInfo(ArrayList<PlaceWeatherTimeBasedata> placeData){
        tripAlarmListAdapter = new TripAlarmListAdapter(getApplicationContext(),tripAlarmItems,isVisitList);
        ArrayList<TripAlarm_rv_item_info> weatherData = new ArrayList<>();
        weatherData.clear();

        double bLatitude, bLongitude;
        for(int i=0;i<placeData.size();i++){
            //API관련 변수
            String placeName = placeData.get(i).getPlaceName();
            String address = placeData.get(i).getPlaceAddress();
            double aLatitude = placeData.get(i).getLatitude();
            double aLongitude = placeData.get(i).getLongitude();
            if((i+1)>=placeData.size()){
                bLatitude = 1000; bLongitude = 1000;
            }
            else{
                bLatitude = placeData.get(i+1).getLatitude();
                bLongitude = placeData.get(i+1).getLongitude();
            }

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
                for(int j=0;j<jsonArray.size();j++){
                    jsonObject4 = jsonArray.get(j).getAsJsonObject();
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
                for(int j=0;j<14;j++){
                    tempValues[j]="null";
                }
                e.printStackTrace();
                return false;
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
            int spendTime; //소요시간 저장
            String spendTime_text; //소요시간(String)

            Log.d("nowMap",":"+aLatitude+","+aLongitude);
            Log.d("nextMap",":"+bLatitude+","+bLongitude);

            if(bLatitude>999||bLongitude>999) {   //다음 행선지가 없는 경우
                spendTime = 0;
                spendTime_text = "null";
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
                    return false;
                }
            }

            //여행시간 정보 정제

            //------------------------------ 시간정보 가져오기 종료 ------------------------------

            //객체 생성
            weatherData.add(new TripAlarm_rv_item_info(placeName, address, tempValues[6], tempValues[3], tempValues[0], rainSnowInfo, iconType, spendTime_text));
            //아이템목록 갱신
            tripAlarmItems = weatherData;


            //경고 알림 푸시
            //날씨 (비, 눈)
            //온도 (33이상, -10이하)
            //B 도착시간 - A 도착시간 <= 소요시간 + @일때 30분 / 20분 / 10분 단위 알림
        }
        return true;
    }
    */

    /*
    //APIWorker결과를 ArrayList<TripAlarm_rv_item_info>로 변환시키는 매서드
    private ArrayList<TripAlarm_rv_item_info> convert_result(String data){
        String[] split_data = data.split(",",2);
        String arraySize = split_data[0];
        int arraySize_i = Integer.parseInt(arraySize);
        String sourcedata = split_data[1];

        ArrayList<TripAlarm_rv_item_info> result = new ArrayList<>();
        String[] items = sourcedata.split(",");

        //TripAlarm_rv_item_info에 필요한 변수들
        String placeName = "";
        String placeAddress = "";
        String placeTmp = "";
        String placeHum = "";
        String placeRainfallProb = "";
        String placeRainSnowInfo = "";
        int placeWeatherIconType = 1;
        String spendTime_text = "";

        result.clear();
        for(int i=0;i<items.length;i++){
            int index = i%8;
            switch(index){    //장소별 정보가 8개씩
                case 0: placeName = items[index]; break;
                case 1: placeAddress = items[index]; break;
                case 2: placeTmp = items[index]; break;
                case 3: placeHum = items[index]; break;
                case 4: placeRainfallProb = items[index]; break;
                case 5: placeRainSnowInfo = items[index]; break;
                case 6: placeWeatherIconType = Integer.parseInt(items[index]); break;
                case 7:
                    spendTime_text = items[index];
                    result.add(new TripAlarm_rv_item_info(placeName, placeAddress, placeTmp, placeHum, placeRainfallProb, placeRainSnowInfo, placeWeatherIconType, spendTime_text));
            }
        }

        return result;
    }
    */

    //경로 정보를 문자열로 바꿔주는 메서드(배열size, 장소이름, 주소, yyyyMMdd, HHmm, xpos, ypos, 현재경도, 현재위도, 다음경도, 다음위도,....")
    private String convert_placeInfo(ArrayList<PlaceWeatherTimeBasedata> basedata){
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

        String arraySize = Integer.toString(basedata.size());
        String basedate = getBaseDate(year_, month_, day_, hour_, minute_);
        String basetime = getBaseTime(hour_,minute_);
        String bLatitude = null;
        String bLongitude = null;

        String result = arraySize;
        for(int i=0;i<basedata.size();i++){
            if((i+1)>=basedata.size()){
                bLatitude = "null"; bLongitude = "null";
            }
            else{
                bLatitude = Double.toString(basedata.get(i+1).getLatitude());
                bLongitude = Double.toString(basedata.get(i+1).getLongitude());
            }
            result = result + "," + basedata.get(i).getPlaceName() + "," + basedata.get(i).getPlaceAddress()
                    + "," + basedate  + "," + basetime
                    + "," + conv_xpos(basedata.get(i).getPlaceAddress())
                    + "," + conv_ypos(basedata.get(i).getPlaceAddress())
                    + "," + basedata.get(i).getLatitude()
                    + "," + basedata.get(i).getLongitude()
                    + "," + bLatitude + "," + bLongitude;

        }
        return result;
    }

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
        if(hour==0||hour==1||(hour==2&&minute<=9)){
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

    //현재시간(YYYY.MM.DD hh:mm) 반환하는 매서드
    private String getNowTime(){
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

        String timedata = ""+year_+"."+month_+"."+day_+" "+hour_+":"+minute_;
        //Log.d("NOW",timedata);

        return timedata;
    }
    //======================================== 내부용 함수 종료 ========================================
}