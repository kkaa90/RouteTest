package com.e.routetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Button button = (Button)findViewById(R.id.goLogin);

        AppDatabase db = AppDatabase.getInstance(this);
        NotifyAppDatabase nDb = NotifyAppDatabase.getInstance(this);

        //new InsertAsyncTask(nDb.notifyRepository()).execute(new Notify(1,"테스트","중입니다"));


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
        Button button1= (Button)findViewById(R.id.goTest);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),TestActivity.class);
                startActivity(intent);
            }
        });

        Button button2= (Button)findViewById(R.id.goTest2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ReviewActivity.class);
                startActivity(intent);
            }
        });

    }
    public static class InsertAsyncTask extends AsyncTask<Notify, Void, Void> {
        private NotifyRepository mNotifyRepository;

        public InsertAsyncTask(NotifyRepository notifyRepository){
            this.mNotifyRepository = notifyRepository;
        }
        @Override
        protected Void doInBackground(Notify... notifies){

            mNotifyRepository.insert(notifies[0]);
            return null;
        }
    }
}