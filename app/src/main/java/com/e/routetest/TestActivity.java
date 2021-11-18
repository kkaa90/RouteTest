package com.e.routetest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private int i=0;
    private SpRepository spRepository;
    AppDatabase db = AppDatabase.getInstance(this);
    public ArrayList<Spot> spots2=new ArrayList<Spot>();
    public ArrayList<Integer> departures2=new ArrayList<Integer>();
    public ArrayList<Integer> arrivals2=new ArrayList<Integer>();
    private int num=0;
    RecyclerView recyclerView;
    ViewRouteAdapter2 viewRouteAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        new Thread(){
            public void run(){
                List<Sp> s = db.spRepository().findAll();
                for(int i=0; i < s.size(); i++) {
                    int sn = s.get(i).getRouteId();
                    System.out.println("======="+sn+"=========");
                    String sArr[] = s.get(i).getSpotsName().split(",");
                    for (int j = 0; j < sArr.length; j++) {
                        System.out.println(sArr[j]);
                    }
                }
            }
        }.start();
        Button tButton = (Button)findViewById(R.id.testButton);
        tButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),TestActivity2.class);
                startActivityForResult(intent,10);

            }
        });
        recyclerView = (RecyclerView)findViewById(R.id.testRV);
        viewRouteAdapter = new ViewRouteAdapter2(getApplicationContext(),spots2,departures2,arrivals2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(viewRouteAdapter);
    }
    @Override
    protected void onActivityResult(int requestCode, int result, Intent data){
        super.onActivityResult(requestCode,result,data);
        if(requestCode==10){
            if(result==RESULT_OK){
                spots2.clear();
                departures2.clear();
                arrivals2.clear();
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
                        for(int i=0;i<sNArr.length;i++){
                            spots2.add(new Spot(Integer.parseInt(sIArr[i]),sNArr[i],Double.parseDouble(sXArr[i]),Double.parseDouble(sYArr[i]),sAArr[i]));
                            departures2.add(-1);
                            arrivals2.add(-1);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("출력");
                                viewRouteAdapter.notifyDataSetChanged();
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