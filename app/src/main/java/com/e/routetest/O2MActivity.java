package com.e.routetest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class O2MActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    O2MAdapter o2MAdapter;
    List<O2MRequest> o2MRequests;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o2_m);
        Button button = (Button)findViewById(R.id.o2mBack);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        o2MRequests = new ArrayList<>();
        o2MRequests.add(new O2MRequest(1,"ttt","gd","gd2","gggg1","gggg1","남자",30));
        o2MRequests.add(new O2MRequest(2,"ttt","gd","gd2","gggg2","gggg2","남자",30));
        o2MRequests.add(new O2MRequest(3,"ttt","gd","gd2","gggg3","gggg3","여자",30));

        recyclerView = (RecyclerView)findViewById(R.id.o2mRv);
        o2MAdapter = new O2MAdapter(getApplicationContext(),o2MRequests);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(o2MAdapter);
    }
}