package com.e.routetest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RouteActivity extends AppCompatActivity {
    private final String DEFAULT = "DEFAULT";
    public static ArrayList<Route> routes= new ArrayList<Route>();
    public static ArrayList<Spot> spots=new ArrayList<Spot>();
    public static ArrayList<Integer> departures=new ArrayList<Integer>();
    public static ArrayList<Integer> arrivals=new ArrayList<Integer>();
    public static ArrayList<Integer> noticeTime = new ArrayList<Integer>();
    public static Context context_main;
    public int var;
    public static int warning =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        context_main=this;
        ViewPager vf = findViewById(R.id.viewPager);
        TabLayout tl = findViewById(R.id.tabLayout);
        PagerAdapter adapter=new ViewPageAdapter(getSupportFragmentManager(), tl.getTabCount());
        vf.setAdapter(adapter);
        vf.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tl));
        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                vf.setCurrentItem(tab.getPosition());
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab){

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab){

            }
        });
        /*spots.add(new Spot(0,"동아대학교",35.11637001672873, 128.9682497981559,"부산광역시 사하구 하단2동 낙동대로550번길 37"));
        spots.add(new Spot(1,"하단역",35.10630701217876, 128.96670639796537,"부산광역시 하단동"));
        spots.add(new Spot(2,"구포시장",35.20956456649422, 129.00355907077622,"부산광역시 북구 구포동 610-11"));
        departures.add(-1);
        departures.add(-1);
        departures.add(-1);
        arrivals.add(-1);
        arrivals.add(-1);
        arrivals.add(-1);*/

        //routes.add(new Route(0,0,"2019-03-31","0,1,2", 0, "10:00,13:00,-","09:00,12:00,15:00"));
        Button goMain = (Button)findViewById(R.id.goBoard);
        Button goRoute = (Button)findViewById(R.id.goRoute);
        goMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),BoardActivity.class);
                startActivity(intent);
            }
        });
        goRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RouteActivity.class);
                startActivity(intent);
            }
        });
        createNotificationChannel(DEFAULT,"default channel",NotificationManager.IMPORTANCE_HIGH);

        Intent intent = new Intent(this,RecommendActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Button noticeButton = (Button) findViewById(R.id.startNotice);
        noticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noticeTime.set(1,3000);
                warning=2;
                new Thread(){
                    public void run(){

                        for(int i=1;i<spots.size();i++){
                            int timenow=getTime(spots.get(i-1),spots.get(i));
                            System.out.println(timenow-noticeTime.get(i-1));
                            if(timenow-noticeTime.get(i-1)>1800){

                                String title="교통 정체 발생";
                                String text = spots.get(i-1).spotName+"에서 "+spots.get(i).spotName+"까지 교통 정체 발생중입니다.\n";
                                createNotification(DEFAULT, 1,title,text,intent);
                            }
                        }
                    }
                }.start();


            }
        });


    }
    void createNotificationChannel(String channelId,String channelName,int importance){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,channelName,importance));
        }
    }
    void createNotification(String channelId,int id, String title, String text, Intent intent){
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,channelId)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(id,builder.build());
    }
    public int getTime(Spot A, Spot B){
        int arrtime=-1;
        double ax=A.spotX;
        double ay=A.spotY;
        double bx=B.spotX;
        double by=B.spotY;

        /*System.out.println("ax : "+ax);
        System.out.println("ay : "+ay);
        System.out.println("bx : "+bx);
        System.out.println("by : "+by);*/
        try {
            String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&mode=transit&origins="
                    +ax+","+ay+"&destinations="+bx+","+by+"&region=KR&key=AIzaSyAn-Tk1TUWDZWTHbdshVkc9z2uQG4dULNQ";
            //String url = "http://3.34.178.98:8080/teamproject/viewAttaction.jsp";
            System.out.println(url);


            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(url).get();
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
            arrtime = jsonObject3.get("value").getAsInt();

            System.out.println(arrtime);

        }catch (Exception e){
            e.printStackTrace();
        }
        return arrtime;
    }

}