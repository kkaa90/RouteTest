package com.e.routetest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import static com.e.routetest.SearchFragment.allSpotList;


public class MainActivity extends AppCompatActivity {
    static String sv = "http://13.209.21.35:8080/teamproject/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button)findViewById(R.id.goLogin);

        AppDatabase db = AppDatabase.getInstance(this);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),RouteActivity.class);
                startActivity(intent);
            }
        });
        Button button1= (Button)findViewById(R.id.goTest);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ShowBoardActivity.class);
                startActivity(intent);
            }
        });

        Button button2= (Button)findViewById(R.id.goTest2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),TestActivity.class);
                startActivity(intent);
            }
        });

    }

}