package com.e.routetest.Workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.e.routetest.TripAlarm_rv_item_info;
import com.e.routetest.trip_alarm_info;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APIWorker extends Worker {
    public APIWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Log.d("APIWORKER_STATE","ACTIVATE");
        //현재는 임시데이터 사용중....
        //String sourceData = "3,동아대,사하구,20211117,0500,96,74,35.11637001672873,128.9682497981559,35.10630701217876,128.96670639796537,하단역,사하구,20211117,0500,96,74,35.10630701217876,128.96670639796537,35.20956456649422,129.00355907077622,구포시장,북구,20211117,0500,96,76,35.20956456649422,129.00355907077622,null,null";

        String sourceData = getInputData().getString("APIWORKER_INPUTDATA");
        String[] split_data = sourceData.split(",",2);
        //Log.d("SOURCEDATA",sourceData);

        String sizeOfArray = split_data[0];      //장소 개수
        int sizeOfArray_i = Integer.parseInt(sizeOfArray);

        String[] items = split_data[1].split(",");

        String placeName = "";
        String placeAddress = "";
        String basedate = "";
        String basetime = "";
        String xpos = "";
        String ypos = "";
        String aLatitude = "";
        String aLongitude = "";
        String bLatitude = "";
        String bLongitude = "";

        ArrayList<trip_alarm_info> placeInfos = new ArrayList<>();

        for(int i=0;i<items.length;i++){
            int j = (i%10);     //장소당 정보 10개
            switch(j){
                case 0: placeName = items[i]; break;
                case 1: placeAddress = items[i]; break;
                case 2: basedate = items[i]; break;
                case 3: basetime = items[i]; break;
                case 4: xpos = items[i]; break;
                case 5: ypos = items[i]; break;
                case 6: aLatitude = items[i]; break;
                case 7: aLongitude = items[i]; break;
                case 8: bLatitude = items[i]; break;
                case 9:
                    bLongitude = items[i];
                    placeInfos.add(new trip_alarm_info(placeName, placeAddress, basedate, basetime, xpos, ypos, aLatitude, aLongitude, bLatitude, bLongitude));
                    break;
            }
        }

        //자료받기
        ArrayList<TripAlarm_rv_item_info> API_info_list = new ArrayList<>();
        for(int i=0;i<sizeOfArray_i;i++){
            API_info_list.add(getAPI(placeInfos.get(i)));
        }

        //특이사항 발생체크
        for(int i=0;i<sizeOfArray_i;i++){
            boolean hasError = false;
            if(API_info_list.get(i).getPlaceWeatherIconType()>=3) {//현재 기상 문제(비,눈) error_alarm_code : 1
                Data error_info = new Data.Builder().putString("error_info","1,"+API_info_list.get(i).getPlaceName() + "," + API_info_list.get(i).getPlaceWeatherIconType()).build();
                hasError = true;
            }
            else if(Double.parseDouble(API_info_list.get(i).getPlaceHum())>=70) {//강수확률 70%이상 error_alarm_code : 2
                Data error_info = new Data.Builder().putString("error_info","2,"+API_info_list.get(i).getPlaceName() + "," + API_info_list.get(i).getPlaceHum()).build();
                hasError = true;
            }
            else if(Double.parseDouble(API_info_list.get(i).getPlaceTmp())>=32
                    || Double.parseDouble(API_info_list.get(i).getPlaceTmp())<= -5) {//온도 32도 이상 or -5도 이하 error_alarm_code : 3
                Data error_info = new Data.Builder().putString("error_info","3,"+API_info_list.get(i).getPlaceName() + "," + API_info_list.get(i).getPlaceTmp()).build();
                hasError = true;
            }

            //시간문제 error_alarm_code : 4

            //특이사항 있을시 푸시
            if(hasError){

            }
        }

        //결과를 TripAlarmViewModel로 넘겨서 갱신


        /*
        //결과를 Data객체에 넣은 뒤 반환
        String result = convert_result(API_info_list);
        Log.d("APIWORKER_RESULT",result);
        Data resultData = new Data.Builder().putString("APIWORKER_RESULT",result).build();
        return Result.success(resultData);
         */
        Log.d("APIWORKER_STATE","FINISH");
        return Result.success();
    }

    //결과를 String으로 만드는 매서드  (장소이름,주소,온도,습도,강수확률,강수/적설량,기상상태아이콘,소요시간(text))
    private String convert_result(ArrayList<TripAlarm_rv_item_info>data){
        String result = Integer.toString(data.size());
        for(int i=0;i<data.size();i++){
            result = result + "," + data.get(i).getPlaceName() + "," + data.get(i).getAddress()
                    + "," + data.get(i).getPlaceTmp() + "," +data.get(i).getPlaceHum()
                    + "," + data.get(i).getPlaceRainfallProb() + "," + data.get(i).getPlaceRainfallInfo()
                    + "," + data.get(i).getPlaceWeatherIconType() + "," + data.get(i).getSpendingTime_text();
        }
        return result;
    }

    //API로 정보 받아오는 매서드
    private TripAlarm_rv_item_info getAPI(trip_alarm_info data){
        // ============================== 날씨정보 받기 시작 ==============================
        String w_API_key = "HEyK4no6dIj6pEs6Qw3Q%2FXnwiyH8MNJcMtcNplODjukvU1f8xdPMo7K2pVcATuJgx1%2BhD8aPCUofv617XqtqOw%3D%3D";  //날씨 API키
        //날씨 API URL
        String w_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=" + w_API_key
                + "&numOfRows=14&pageNo=1&dataType=JSON&base_date=" + data.getBasedate()
                + "&base_time=" + data.getBasetime()
                + "&nx=" + data.getXpos()
                + "&ny=" + data.getYpos();
        //Log.d("w_URL",w_URL);

        String[] w_values = new String[14]; //날씨정보 보관 배열

        try {
            OkHttpClient client = new OkHttpClient();

            Request.Builder builder = new Request.Builder().url(w_URL).get();
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

                w_values[index_num] = tempStr;

                //Log.d("API_INFO",w_values[index_num] +","+ index_num);
            }
        }
        catch(Exception e){
            for(int i=0;i<14;i++){
                w_values[i]="null";
            }
            e.printStackTrace();
        }


        //날씨정보정제
        boolean isRain = true;
        int iconType = -1;
        String rainSnowInfo = " ";

        //날씨 SKY(5), PTY(1) = iconType
        switch(w_values[1]){
            //강수형태 없음
            case "0":{
                switch(w_values[5]){
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
        rainSnowInfo = (isRain)?w_values[2]:w_values[4];    //강수랑or적설량
        /*
        if(isRain){ //비
            if(w_values[2]!=null)
                rainSnowInfo = "강수량 : " + w_values[2];
            else
                rainSnowInfo = "강수량 : 0mm";
        }
        else{
            if(w_values[4]!=null)
                rainSnowInfo = "적설량 : " + w_values[4];
            else
                rainSnowInfo = "적설량 : 0cm";
        }
         */
        // ============================== 날씨정보 받기 종료 ==============================

        // ============================== 시간정보 받기 시작 ==============================
        //소요시간 API URL
        String t_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&mode=transit&origins="
                +data.getaLatitude()+","+data.getaLongitude()+"&destinations="+data.getbLatitude()+","+data.getbLongitude()+"&region=KR&key=AIzaSyAn-Tk1TUWDZWTHbdshVkc9z2uQG4dULNQ";
        //Log.d("TIME_URL",t_URL);
        int spendTime;  //소요시간(int)
        String spendTime_text;  //소요시간(String)

        //다음 행선지가 없는 경우
        if(data.getbLatitude().equals("null")){
            spendTime = 0;
            spendTime_text = "null";
        }else{
            try{
                //Log.d("TIMEURL",t_URL);

                OkHttpClient client = new OkHttpClient();
                Request.Builder builder = new Request.Builder().url(t_URL).get();
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

                //Log.d("API_SPENDTIME","0"+spendTime);
                //Log.d("SPI_SPENDTIMEDETAIL",spendTime_text);
            }catch(Exception e){
                spendTime = -1;
                spendTime_text = "error";
                e.printStackTrace();
            }
        }
        // ============================== 시간정보 받기 종료 ==============================

        return new TripAlarm_rv_item_info(data.getPlaceName(),data.getPlaceAddress(), w_values[6],w_values[3],w_values[0],rainSnowInfo,iconType,spendTime_text);
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
