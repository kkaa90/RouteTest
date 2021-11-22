package com.e.routetest;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeFragmentSpAdapter extends RecyclerView.Adapter<HomeFragmentSpAdapter.Holder> {
    private Context context;
    private ArrayList<Sp> routeList = new ArrayList<>();

    public HomeFragmentSpAdapter(Context context, ArrayList<Sp> routeList) {
        this.context = context;
        this.routeList = routeList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.homefragment_route_rv_item,parent,false);
        Holder holder = new Holder(view);

        return holder;
    }

    //item을 recyclerview에 bind해주는 함수
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.onBind(routeList.get(position),position);
    }

    @Override
    public int getItemCount() {return routeList.size();}


    //Holder : recyclerview의 subView를 setting해주는 곳
    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Sp sp;
        private int position;

        public TextView firstPlace;
        public TextView lastPlace;
        public Button goButton;

        //Holder 생성자
        public Holder(@NonNull View view) {
            super(view);

            firstPlace = (TextView)view.findViewById(R.id.home_route_rv_start);
            lastPlace = (TextView)view.findViewById(R.id.home_route_rv_goal);
            goButton = (Button)view.findViewById(R.id.home_route_rv_button);

        }

        //onBindViewHolder에서 bind시켜주는 함수
        void onBind(Sp sp, int position){
            this.sp = sp;
            this.position = position;

            String[] placeNames = sp.getSpotsName().split(",");
            int lastIndex = placeNames.length - 1;

            String s_place = placeNames[0];
            String l_place = placeNames[lastIndex];

            firstPlace.setText(s_place);
            lastPlace.setText(l_place);

            goButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //버튼 클릭시 해당 경로정보를 임시db에 저장후 TripAlarmListActivity로 이동
            new TripAlarmComponent().save_Sp_on_TRouteDB(sp,context);
            Intent intent = new Intent(context,TripAlarmListActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        }
    }
}
