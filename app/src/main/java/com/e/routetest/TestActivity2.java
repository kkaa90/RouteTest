package com.e.routetest;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

public class TestActivity2 extends Activity {
    private List<Sp> s = new ArrayList<>();
    private SpRepository spRepository;
    public static Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_test2);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.testRV2);
        ViewTestAdapter viewTestAdapter=new ViewTestAdapter(getApplicationContext(),s);
        mContext=this;
        AppDatabase db = AppDatabase.getInstance(this);


        new Thread(){
            public void run(){
                s.addAll(db.spRepository().findAll());
                //s.addAll(s2);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewTestAdapter.notifyDataSetChanged();
                    }
                });
            }
        }.start();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(viewTestAdapter);

    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

}