package com.e.routetest;

import android.content.Context;
import android.os.AsyncTask;
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

//TripAlarm 관련 함수 및 변수 모음
public class TripAlarmComponent {
    public static double STANDARD_MAX_TEMPERUATURE = 32;     //알림 기준 최대온도
    public static double STANDARD_MIN_TEMPERTATURE = -5;     //알림 기준 최저온도
    public static double STANDARD_HUMIDITY = 70;             //알림 기준 습도


    //특이사항 발생시 알림 푸쉬
    public void tripAlarmPush(TripAlarm_rv_item_info basedata, Context context){
        if(basedata.getPlaceTmp().equals("null")){
            Log.e("tripAlarmPush Data Missing","APIWORK could not get data from API");
            return;
        }
        String errorTitle = null;
        String errorBody = null;

        if(basedata.getPlaceWeatherIconType()>=3) {             //현재 기상 문제(비,눈)
            errorTitle = basedata.getPlaceName()+"에 기상 경고";
            errorBody = "현재 "+basedata.getPlaceName()+"에 ";
            if(basedata.getPlaceWeatherIconType()==3){
                errorBody = errorBody + "비가 오고 있습니다.";
            }
            else{
                errorBody = errorBody + "눈이 오고 있습니다.";
            }
            NotifyAppDatabase.getInstance(context).notifyRepository().insert(new Notify(2,errorTitle,errorBody));
        }
        else if(Double.parseDouble(basedata.getPlaceHum()) >= STANDARD_HUMIDITY) {         //강수확률 70%이상
            errorTitle = basedata.getPlaceName()+"에 강수 확률 경고";
            errorBody = "현재 "+basedata.getPlaceName()+"에 강수확률이 " + basedata.getPlaceRainfallProb()+"% 입니다.";
            NotifyAppDatabase.getInstance(context).notifyRepository().insert(new Notify(2,errorTitle,errorBody));
        }
        else if(Double.parseDouble(basedata.getPlaceTmp()) >= STANDARD_MIN_TEMPERTATURE
                || Double.parseDouble(basedata.getPlaceTmp()) <= STANDARD_MAX_TEMPERUATURE) {         //온도 32도 이상 or -5도 이하
            errorTitle = basedata.getPlaceName()+"에 기온 경고";
            errorBody = "현재 "+basedata.getPlaceName()+"에 기온이 "+ basedata.getPlaceTmp()+"°C입니다.";
            NotifyAppDatabase.getInstance(context).notifyRepository().insert(new Notify(2,errorTitle,errorBody));
        }

        //시간 관련 문제
    }

    @NotNull
    public TripAlarm_rv_item_info getItemInfo(
            String placeName, String address, double placeLatitude, double placeLongitude,
            double nextLatitude, double nextLongitude, String arrivalTime, String nextArrivalTime){

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
            }
        }

        //여행시간 정보 정제
        String moveTime; //이동을 시작해야될 최소시간
        String remainTime;  //남은시간
        if(spendTime_text.equals("null")){
            moveTime = "null";
            remainTime = "null";
        }
        else{
            moveTime = calc_timeDifference(nextArrivalTime,Integer.toString(spendTime),true);
            Log.d("TIMENOW",getTime());
            remainTime = calc_timeDifference(moveTime, getTime(),false);
        }
        Log.d("MOVETIME",moveTime);
        Log.d("REMAINTIME",remainTime);

        //------------------------------ 시간정보 가져오기 종료 ------------------------------

        //객체 생성
        TripAlarm_rv_item_info tempItem
                = new TripAlarm_rv_item_info(placeName, address, arrivalTime, tempValues[6], tempValues[3], tempValues[0], rainSnowInfo, iconType, spendTime_text, moveTime, remainTime);


        //경고 알림 푸시
        //날씨 (비, 눈)
        //온도 (33이상, -10이하)
        //B 도착시간 - A 도착시간 <= 소요시간 + @일때 30분 / 20분 / 10분 단위 알림

        return tempItem;
    }

    //시간차 계산 (시간은 1300같이 붙여씀) false : HHmm - HHmm / ture : HHmm - ss
    public String calc_timeDifference(String timeA, String timeB, boolean isTimeBss){
        int timeA_hour = Integer.parseInt(timeA.substring(0,2));
        int timeA_minute = Integer.parseInt(timeA.substring(2));
        int timeB_hour, timeB_minute;
        int hour_diff, minute_diff;


        if(isTimeBss){
            int temp = Integer.parseInt(timeB);

            timeB_hour = temp/3600;
            int temp1 = temp%3600;
            timeB_minute = temp1/60;
            if(temp1%60>0){
                timeB_minute += 1;
            }
        }
        else{
            timeB_hour = Integer.parseInt(timeB.substring(0,2));
            timeB_minute = Integer.parseInt(timeB.substring(2));
        }


        hour_diff = timeA_hour - timeB_hour;
        minute_diff = timeA_minute - timeB_minute;

        if(minute_diff<0){
            hour_diff--;
            minute_diff = 60 + minute_diff;
        }

        String result = "";

        if(hour_diff<0){
            result="지각";
        }
        else{
            if(hour_diff<10){
                result = result +"0"+hour_diff;
            }
            else{
                result = result + hour_diff;
            }

            if(minute_diff<10){
                result = result +"0"+minute_diff;
            }
            else{
                result = result + minute_diff;
            }
        }
        return result;
    }

    //주소를 구,군단위로 변환
    public String split_Str(String source){
        String[] tmp = source.split(" ",2);
        String tmp2 = tmp[1];
        String[] tmp3 = tmp2.split("구|군",2);
        String result =  tmp3[0];

        if(result.equals("기장")){
            result += "군";
        }
        else{
            result += "구";
        }

        return result;
    }

    //기존 경로(이름,주소,위도,경도,도착시간)을 string으로 변환
    public String encoude_Route(ArrayList<PlaceWeatherTimeBasedata> routeData){
        int listSize = routeData.size();
        String result = Integer.toString(listSize);

        for(int i=0;i<listSize;i++){
            result = result + "," + routeData.get(i).getPlaceName()
                    + "," + routeData.get(i).getPlaceAddress()
                    + "," + routeData.get(i).getLatitude()
                    + "," + routeData.get(i).getLongitude()
                    + "," + routeData.get(i).getArrivalTime();
        }

        return result;
    }

    public ArrayList<PlaceWeatherTimeBasedata> decode_Route(String routeData){
        ArrayList<PlaceWeatherTimeBasedata> result = new ArrayList<>();

        String[] temp1 = routeData.split(",",2);
        int arraySize = Integer.parseInt(temp1[0]);

        result.clear();

        String placeName = null;
        String placeAddress = null;
        double latitude = 0;
        double longitude = 0;
        String arrivalTime = null;
        String[] temp2 = temp1[1].split(",");
        for(int i=0;i<temp2.length;i++){
            int j = i%5;
            switch(j){
                case 0: placeName = temp2[i]; break;
                case 1: placeAddress = temp2[i]; break;
                case 2: latitude = Double.parseDouble(temp2[i]); break;
                case 3: longitude = Double.parseDouble(temp2[i]); break;
                case 4:
                    arrivalTime = temp2[i];
                    result.add(new PlaceWeatherTimeBasedata(placeName,placeAddress,latitude,longitude,arrivalTime));
            }
        }

        return result;
    }

    //경로 정보를 문자열로 바꿔주는 메서드 APIWORKER 데이터 전달용(배열size, 장소이름, 주소, 현재경도, 현재위도, 다음경도, 다음위도, 도착시간, 다음도착시간....")
    public String convert_placeInfo(ArrayList<PlaceWeatherTimeBasedata> basedata){
        String arraySize = Integer.toString(basedata.size());
        String address = null;
        String bLatitude = null;
        String bLongitude = null;
        String nextArrivalTime = null;

        String result = arraySize;
        for(int i=0;i<basedata.size();i++){
            if((i+1)>=basedata.size()){
                bLatitude = "null"; bLongitude = "null";
                nextArrivalTime = "null";
            }
            else{
                bLatitude = Double.toString(basedata.get(i+1).getLatitude());
                bLongitude = Double.toString(basedata.get(i+1).getLongitude());
                nextArrivalTime = basedata.get(i+1).getArrivalTime();
            }
            address = split_Str(basedata.get(i).getPlaceAddress());
            result = result + "," + basedata.get(i).getPlaceName() + "," + address
                    + "," + basedata.get(i).getLatitude() + "," + basedata.get(i).getLongitude()
                    + "," + bLatitude + "," + bLongitude
                    + "," + basedata.get(i).getArrivalTime() + "," + nextArrivalTime;

        }
        return result;
    }

    //주소 -> 자체 격자 좌표 변환
    public String conv_xpos(String address){
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

    public String conv_ypos(String address){
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
    public String getBaseDate(String year_, String month_, String day_, String hour_, String minute_){
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
    public String getBaseTime(String hour_, String minute_){
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
    public int dataMapping(String dataType){
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

    //현재시간(hh
    public String getTime(){//시간 관련 변수
        long mNow = System.currentTimeMillis();
        Date mReDate = new Date(mNow);
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
        SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
        String hour_ = hourFormat.format(mReDate);
        String minute_ = minuteFormat.format(mReDate);

        String time = ""+hour_+minute_;
        return time;
    }

    //현재시간(YYYY.MM.DD hh:mm) 반환하는 매서드
    public String getNowTime(){
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
