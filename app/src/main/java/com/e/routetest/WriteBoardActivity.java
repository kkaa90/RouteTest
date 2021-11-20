package com.e.routetest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.e.routetest.LoadingActivity.sv;
import static com.e.routetest.LoginActivity.userId;
import static com.e.routetest.RouteActivity.arrivals;
import static com.e.routetest.RouteActivity.departures;

public class WriteBoardActivity extends AppCompatActivity {
    private SpRepository spRepository;
    AppDatabase db = AppDatabase.getInstance(this);
    public ArrayList<Spot> spots2=new ArrayList<Spot>();
    public ArrayList<Integer> departures2=new ArrayList<Integer>();
    public ArrayList<Integer> arrivals2=new ArrayList<Integer>();
    public ArrayList<Route> routes2= new ArrayList<Route>();
    private int num=0;
    TextView rEdit;
    TextView dEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_write_board);
        TextInputEditText tEdit = (TextInputEditText)findViewById(R.id.titleEdit);
        TextView wEdit = (TextView) findViewById(R.id.writerEdit);
        rEdit = (TextView)findViewById(R.id.routeNum);
        dEdit = (TextView)findViewById(R.id.datePick);
        TextInputEditText cEdit = (TextInputEditText)findViewById(R.id.contentEdit);
        TextInputEditText lEdit = (TextInputEditText)findViewById(R.id.linkEdit);
        wEdit.setText(userId);
        Button writeB = (Button)findViewById(R.id.participation);
        rEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),TestActivity2.class);
                startActivityForResult(intent,100);
            }
        });
        dEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int mYear = cal.get(Calendar.YEAR);
                int mMonth = cal.get(Calendar.MONTH);
                int mDay = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(WriteBoardActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dom) {
                        dEdit.setText(year+"-"+(month+1)+"-"+dom);
                    }
                },mYear,mMonth,mDay);
                datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                datePickerDialog.show();
            }
        });
        writeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    public void run(){
                        if(writeBoard(tEdit.getText().toString(),wEdit.getText().toString(),routes2.get(0),cEdit.getText().toString().replaceAll("\\n", "<br />"),
                                lEdit.getText().toString(),dEdit.getText().toString())){
                            finish();
                        }
                        else{

                        }
                    }
                }.start();
            }
        });




    }
    private boolean writeBoard(String t, String w, Route route, String c, String l, String d){
        try {

            int r = writeRoute(route);
            if(r==-1) return false;

            String url = sv + "writeBoard.jsp?routeID="+r+"&userID="+w
                    +"&maxP=4&appliT="+d+"&boardTitle="+t+"&boardContent="+c+"&kakaoLink="+l;
            System.out.println(url);


            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(url).get();
            Request request = builder.build();

            Response response = client.newCall(request).execute();
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(response.body().string());
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String success = jsonObject.get("success").getAsString();
            System.out.println(success);

            if(success.equals("true")){ return true;}
            else {
                //new AlertDialog.Builder(WriteBoardActivity.this).setMessage("작성 실패").show();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    private int writeRoute(Route route) {


        try {
            String url = sv + "writeRoute.jsp?userID=" + route.userId
                    + "&routeTitle=테스트&routeList=" + route.spots + "&Thema=" + route.theme + "&arriveTime=" + route.arrivalTime;
            System.out.println(url);


            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(url).get();
            Request request = builder.build();

            Response response = client.newCall(request).execute();
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(response.body().string());
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String success = jsonObject.get("success").getAsString();
            System.out.println(success);
            if(success.equals("true")) {
                int rn = jsonObject.get("routeID").getAsInt();
                return rn;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;

    }
    protected void onActivityResult(int requestCode, int result, Intent data){
        super.onActivityResult(requestCode,result,data);
        if(requestCode==100){
            if(result==RESULT_OK){
                departures2.clear();
                arrivals2.clear();
                num=data.getIntExtra("result",0);
                System.out.println(num);
                new Thread(){
                    public void run(){
                        Sp s = db.spRepository().findById(num);
                        int rs = routes2.size();
                        String sArr[] = s.getSpotsName().toString().split(",");
                        for(int i=0;i<sArr.length;i++){
                            departures2.add(-1);
                            arrivals2.add(-1);
                        }
                        String depList = String.valueOf(departures2.get(0));
                        for (int i = 1; i < departures2.size(); i++) {
                            depList = depList + ",";
                            depList = depList + String.valueOf(departures2.get(i));
                        }
                        String arrList = String.valueOf(arrivals2.get(0));
                        for (int i = 1; i < arrivals2.size(); i++) {
                            arrList = arrList + ",";
                            arrList = arrList + String.valueOf(arrivals2.get(i));
                        }
                        routes2.add(new Route(rs,userId,"2021-11-18",s.getSpotsId().toString(),"0",depList,arrList));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("출력");
                                String sss = sArr[0]+" -> "+sArr[sArr.length-1];
                                rEdit.setText(sss);
                            }
                        });
                    }
                }.start();
            }
            else{
                num=-1;
            }
        }

    }


}