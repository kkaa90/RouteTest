package com.e.routetest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import android.os.Bundle;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {

    private SpRepository spRepository;
    private int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        AppDatabase db = AppDatabase.getInstance(this);
        new Thread(){
            public void run(){
                Sp s = db.spRepository().findById(1);
                String sArr[] = s.getSpotsName().split(",");
                for(int i=0;i<sArr.length;i++){
                    System.out.println(sArr[i]);
                }
            }
        }.start();
    }
}