package com.e.routetest;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

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

        public TextView serial;
        public TextView firstPlace;
        public TextView firstTime;
        public TextView lastPlace;
        public TextView lastTime;
        public ConstraintLayout goButton;

        //Holder 생성자
        public Holder(@NonNull View view) {
            super(view);

            serial = (TextView)view.findViewById(R.id.trip_alarm_rv_item_serial);

            firstPlace = (TextView)view.findViewById(R.id.home_route_rv_start);
            firstPlace.setSingleLine(true);
            firstPlace.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            firstPlace.setSelected(true);

            lastPlace = (TextView)view.findViewById(R.id.home_route_rv_goal);
            lastPlace.setSingleLine(true);
            lastPlace.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            lastPlace.setSelected(true);

            goButton = (ConstraintLayout) view.findViewById(R.id.home_route_rv_main);

        }

        //onBindViewHolder에서 bind시켜주는 함수
        void onBind(Sp sp, int position){
            this.sp = sp;
            this.position = position;

            //serial.setText("Tripvision Ticket "+(position+1));

            TripAlarmComponent component = new TripAlarmComponent();

            String[] placeNames = sp.getSpotsName().split(",");
            String[] arrivalTimes = sp.getArrTime().split(",");
            int lastIndex = placeNames.length - 1;

            String s_place = placeNames[0];
            String l_place = placeNames[lastIndex];

            int s_arrivalTime = Integer.parseInt(arrivalTimes[0]);
            int l_arrivalTime = Integer.parseInt(arrivalTimes[lastIndex]);
            int s_hour = s_arrivalTime/3600;
            int s_minute = (s_arrivalTime%3600)/60;
            int l_hour = l_arrivalTime/3600;
            int l_minute = (l_arrivalTime%3600)/60;

            Log.d("HOME S_TIME",""+s_hour+s_minute);

            firstPlace.setText(s_place);
            lastPlace.setText(l_place);

            //firstTime.setText(""+(s_arrivalTime/3600)+":"+((s_arrivalTime%3600)/60));
            //lastTime.setText(""+(l_arrivalTime/3600)+":"+((l_arrivalTime%3600)/60));

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
