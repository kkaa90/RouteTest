package com.e.routetest;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static com.e.routetest.LoadingActivity.sv;
import static com.e.routetest.RouteActivity.arrivals;
import static com.e.routetest.RouteActivity.departures;
import static com.e.routetest.RouteActivity.noticeTime;
import static com.e.routetest.RouteActivity.routes;
import static com.e.routetest.RouteActivity.spots;

public class EditRouteFragment extends Fragment {

    int hour = 0;
    int min = 0;
    int num = 0;
    EditRouteAdapter editRouteAdapter;
    public void postToastMessage(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_route, container, false);
        Button timeButton = (Button) view.findViewById(R.id.getTimeButton);
        Button showButton = (Button) view.findViewById(R.id.showTimeButton);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.viewRoute);
        recyclerView.setHasFixedSize(true);
        editRouteAdapter = new EditRouteAdapter(getActivity(), spots, departures, arrivals);
        EditText editText = (EditText) view.findViewById(R.id.editTextTime);
        AppDatabase db = AppDatabase.getInstance(getContext());
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    public void run() {
                        departures.clear();
                        arrivals.clear();
                        arrivals.add(hour * 3600 + min * 60);
                        for (int i = 1; i < spots.size(); i++) {
                            int a = RouteFragment.getTime(spots.get(i - 1), spots.get(i));
                            departures.add(arrivals.get(i - 1) + 3600);
                            arrivals.add(departures.get(i - 1) + a);
                            noticeTime.add(a);
                        }
                        departures.add(-1);
                        postToastMessage("소요시간 계산 완료");
                    }
                }.start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editRouteAdapter.notifyDataSetChanged();
                    }
                }, 2000);
            }
        });
        showButton.setOnClickListener(new View.OnClickListener() {
            final CharSequence[] items = {"목록 비우기","저장","불러오기"};
            @Override
            public void onClick(View view) {
                AlertDialog.Builder sDialog = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                sDialog.setTitle("선택").setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int select) {
                        if(select==0){
                            spots.clear();
                            departures.clear();
                            arrivals.clear();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    editRouteAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        else if(select==1) {
                            if (spots.size() == 0) {

                            } else {
                                for (int i = 0; i < spots.size(); i++) {
                                    System.out.println("arrival : " + arrivals.get(i) / 3600 + ":" + (arrivals.get(i) % 3600) / 60);
                                    System.out.println("departure : " + departures.get(i) / 3600 + ":" + (departures.get(i) % 3600) / 60);

                                }
                                editRouteAdapter.notifyDataSetChanged();
                                for (int i = 0; i < spots.size(); i++) {
                                    Spot now = spots.get(i);
                                    System.out.println(now.spotID + "," + now.spotName + "," + String.valueOf(now.spotX) + "," + String.valueOf(now.spotY) + "," + now.spotAddress);
                                }
                                int s = routes.size();
                                String userId = "guest1";
                                String routeDate = "2021-06-08";
                                String spotList = String.valueOf(spots.get(0).spotID);
                                for (int i = 1; i < spots.size(); i++) {
                                    spotList = spotList + ",";
                                    spotList = spotList + String.valueOf(spots.get(i).spotID);
                                }
                                String theme = "0";
                                String depList = String.valueOf(departures.get(0));
                                for (int i = 1; i < departures.size(); i++) {
                                    depList = depList + ",";
                                    depList = depList + String.valueOf(departures.get(i));
                                }
                                String arrList = String.valueOf(arrivals.get(0));
                                for (int i = 1; i < arrivals.size(); i++) {
                                    arrList = arrList + ",";
                                    arrList = arrList + String.valueOf(arrivals.get(i));
                                }
                                routes.add(new Route(s, userId, routeDate, spotList, theme, depList, arrList));
                                for (int i = 0; i < routes.size(); i++) {
                                    Route route = routes.get(i);
                                    System.out.println(route.routeId);
                                    System.out.println(route.userId);
                                    System.out.println(route.routeDate);
                                    System.out.println(route.spots);
                                    System.out.println(route.theme);
                                    System.out.println(route.departureTime);
                                    System.out.println(route.arrivalTime);
                                }
                                String spName = String.valueOf(spots.get(0).spotName);
                                for (int i = 1; i < spots.size(); i++) {
                                    spName = spName + ",";
                                    spName = spName + String.valueOf(spots.get(i).spotName);
                                }
                                String spX = String.valueOf(spots.get(0).spotX);
                                for (int i = 1; i < spots.size(); i++) {
                                    spX = spX + ",";
                                    spX = spX + String.valueOf(spots.get(i).spotX);
                                }
                                String spY = String.valueOf(spots.get(0).spotY);
                                for (int i = 1; i < spots.size(); i++) {
                                    spY = spY + ",";
                                    spY = spY + String.valueOf(spots.get(i).spotY);
                                }
                                String spA = String.valueOf(spots.get(0).spotAddress);
                                for (int i = 1; i < spots.size(); i++) {
                                    spA = spA + ",";
                                    spA = spA + String.valueOf(spots.get(i).spotAddress);
                                }
                                new InsertAsyncTask(db.spRepository()).execute(new Sp(spName, spotList, spX, spY, spA, "-1", arrList, depList));
                            }
                        }
                        else{
                            Intent intent = new Intent(getActivity().getApplicationContext(),TestActivity2.class);
                            startActivityForResult(intent,10);
                        }
                    }
                }).show();



            }


        });
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcTime = Calendar.getInstance();
                int mcHour = mcTime.get(Calendar.HOUR_OF_DAY);
                int mcMin = mcTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int h, int m) {
                        String time = String.valueOf(h) + ":";
                        if (h < 10) time = "0" + time;
                        if (m < 10) time = time + "0";
                        time = time + m;
                        editText.setText(time);
                        hour = h;
                        min = m;
                    }
                }, mcHour, mcMin, true);
                mTimePicker.setTitle("시간 선택");
                mTimePicker.show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(editRouteAdapter);
        return view;
    }

    private void writeRoute(Route route) {


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


        } catch (Exception e) {
            e.printStackTrace();
        }
        return;

    }

    public static class InsertAsyncTask extends AsyncTask<Sp, Void, Void> {
        private SpRepository mSpRepository;

        public InsertAsyncTask(SpRepository spRepository){
            this.mSpRepository = spRepository;
        }
        @Override
        protected Void doInBackground(Sp... sps){

            mSpRepository.insert(sps[0]);
            return null;
        }
    }
    public void onActivityResult(int requestCode, int result, Intent data){
        AppDatabase db = AppDatabase.getInstance(getActivity().getApplicationContext());
        super.onActivityResult(requestCode,result,data);
        if(requestCode==10){
            if(result==RESULT_OK){
                spots.clear();
                departures.clear();
                arrivals.clear();
                num=data.getIntExtra("result",0);
                System.out.println(num);
                new Thread(){
                    public void run(){
                        Sp s = db.spRepository().findById(num);
                        String sNArr[] = s.getSpotsName().split(",");
                        String sIArr[] = s.getSpotsId().split(",");
                        String sXArr[] = s.getSpotsX().split(",");;
                        String sYArr[] = s.getSpotsY().split(",");
                        String sAArr[] = s.getSpotsAddress().split(",");
                        String dArr[] = s.getDesTime().split(",");
                        String aArr[] = s.getArrTime().split(",");
                        for(int i=0;i<sNArr.length;i++){
                            spots.add(new Spot(Integer.parseInt(sIArr[i]),sNArr[i],Double.parseDouble(sXArr[i]),Double.parseDouble(sYArr[i]),sAArr[i]));
                            departures.add(Integer.parseInt(dArr[i]));
                            arrivals.add(Integer.parseInt(aArr[i]));
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("출력");
                                editRouteAdapter.notifyDataSetChanged();
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