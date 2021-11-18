package com.e.routetest;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TripAlarmListAdapter extends RecyclerView.Adapter<TripAlarmListAdapter.Holder>{
    private Context context;
    private ArrayList<TripAlarm_rv_item_info> tripAlarmDataList = new ArrayList<TripAlarm_rv_item_info>();;    //여행경로 데이터
    private ArrayList<Boolean> isVisitList; //여행지 방문 정보
    //item의 클릭 상태를 저장할 array 객체
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    //직전에 클릭됐던 item의 position
    private int prePosition = -1;

    //public TripAlarmListAdapter(Context context, ArrayList<TripAlarm_rv_item_info> tripAlarmDataList, ArrayList<Boolean> isVisitList){
    public TripAlarmListAdapter(Context context, ArrayList<TripAlarm_rv_item_info> tripAlarmDataList){
        this.context = context;
        this.tripAlarmDataList = tripAlarmDataList;
        //this.isVisitList = isVisitList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_alarm_rv_item,parent,false);
        Holder holder = new Holder(view);

        return holder;
    }

    //item을 recyclerview에 bind해주는 함수
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.onBind(tripAlarmDataList.get(position),position);
    }

    @Override
    public int getItemCount() {return tripAlarmDataList.size();}

    //기존리스트 삭제후 새로운 리스트로 업데이트. 이후 변경알림
    public void updateItemList(final ArrayList<TripAlarm_rv_item_info> newDataList){
       this.tripAlarmDataList = newDataList;
        notifyDataSetChanged();
    }

    //Holder : recyclerview의 subView를 setting해주는 곳
    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView weatherIcon;
        public TextView placeName;
        public TextView placeTmp;
        public TextView placeHum;
        public TextView placeRainfallProb;
        public TextView placeRainfallInfo;

        public TextView placeSpendTime;

        public Button visitButton;
        public Button testButton;

        public Button changeButton;
        public LinearLayout item_detail;
        public LinearLayout item_info;

        private TripAlarm_rv_item_info tripAlarm_rv_item_info;
        private int position;

        //Holder 생성자
        public Holder(@NonNull View view) {
            super(view);
            weatherIcon = (ImageView)view.findViewById(R.id.trip_alarm_rv_item_weatherIcon);
            placeName = (TextView)view.findViewById(R.id.trip_alarm_rv_item_placeName);
            placeTmp = (TextView)view.findViewById(R.id.trip_alarm_rv_item_temperauterInfo);
            placeHum = (TextView)view.findViewById(R.id.trip_alarm_rv_item_HumidityInfo);
            placeRainfallProb = (TextView)view.findViewById(R.id.trip_alarm_rv_item_rainfallProbablityInfo);
            placeRainfallInfo = (TextView)view.findViewById(R.id.trip_alarm_rv_item_rainfallInfo);

            placeSpendTime = (TextView)view.findViewById(R.id.trip_alarm_rv_item_spendTime);

            visitButton = (Button)view.findViewById(R.id.trip_alarm_rv_item_visitButton);
            testButton = (Button)view.findViewById(R.id.trip_alarm_rv_item_testButton);

            item_detail = (LinearLayout)view.findViewById(R.id.trip_alarm_rv_item_detail);
            item_info = (LinearLayout)view.findViewById(R.id.trip_alarm_rv_item_infoList);
            changeButton = (Button)view.findViewById(R.id.trip_alarm_rv_item_changeButton);
        }

        //onBindViewHolder에서 bind시켜주는 함수
        void onBind(TripAlarm_rv_item_info tripAlarm_rv_item_info, int position){
            this.tripAlarm_rv_item_info = tripAlarm_rv_item_info;
            this.position = position;

            int temp = tripAlarm_rv_item_info.getPlaceWeatherIconType();
            switch (temp){  //맑음0 구름많음1 흐림2 비3 비/눈4 눈5 소나기6
                case 0: weatherIcon.setImageResource(R.drawable.sunny); break;
                case 1: weatherIcon.setImageResource(R.drawable.littlecloudy); break;
                case 2: weatherIcon.setImageResource(R.drawable.cloudy); break;
                case 3: weatherIcon.setImageResource(R.drawable.rain); break;
                case 4: weatherIcon.setImageResource(R.drawable.rainandsnow); break;
                case 5: weatherIcon.setImageResource(R.drawable.snow); break;
                case 6: weatherIcon.setImageResource(R.drawable.shower); break;
            }
            placeName.setText("장소 : " + tripAlarm_rv_item_info.getPlaceName());
            placeTmp.setText("온도 : " + tripAlarm_rv_item_info.getPlaceTmp());
            placeHum.setText("습도 : " + tripAlarm_rv_item_info.getPlaceHum() + "%");
            placeRainfallProb.setText("강수확률 : " + tripAlarm_rv_item_info.getPlaceRainfallProb() + "%");
            placeRainfallInfo.setText(tripAlarm_rv_item_info.getPlaceRainfallInfo());

            if(tripAlarm_rv_item_info.getSpendingTime_text()=="없음") {
                placeSpendTime.setText("마지막 행선지입니다.");
            }else {
                placeSpendTime.setText("소요시간 : 약" + tripAlarm_rv_item_info.getSpendingTime_text());
            }

            changeVisibility(selectedItems.get(position));

            item_info.setOnClickListener(this);
            changeButton.setOnClickListener(this);
            visitButton.setOnClickListener(this);
            testButton.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.trip_alarm_rv_item_infoList:
                    if(selectedItems.get(position)){
                        //펼쳐진 item을 클릭 시
                        selectedItems.delete(position);
                    }else{
                        //직전의 클릭됐던 item의 클릭상태를 지움
                        selectedItems.delete(prePosition);
                        //클릭한 item의 position을 저장
                        selectedItems.put(position,true);
                    }
                    //해당 포지션의 변화를 알림
                    if(prePosition != -1) notifyItemChanged(prePosition);
                    notifyItemChanged(position);
                    //클릭된 position 저장
                    prePosition = position;
                    break;

                case R.id.trip_alarm_rv_item_changeButton:
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setTitle("여행지 변경 확인");
                    builder.setMessage("여행지를 변경하시겠습니까?");
                    builder.setPositiveButton("예",null);
                    builder.setNegativeButton("아니오",null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    break;

                case R.id.trip_alarm_rv_item_visitButton:
                    isVisitList.set(position,true);
                    //item_detail.setBackgroundColor(Color.RED);
                    Log.d("VISITBUTTONACTIVE","OK");
                    Log.d("ISVISITLISTSIZE",""+isVisitList.size());
                    break;

                case R.id.trip_alarm_rv_item_testButton:
                    Log.d("ISVISIT?",isVisitList.get(position)?"T":"F");
                    break;
            }
        }

        //view 확장을 위한 메소드
        private void changeVisibility(final boolean isExpanded){
            //height 값을 dp로 지정해서 넣고싶으면 아래 소스를 이용
            int dpValue = 130;
            float d = context.getResources().getDisplayMetrics().density;
            int height = (int)(dpValue*d);

            //ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int배열
            ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0,height) : ValueAnimator.ofInt(height,0);
            //Animation이 실행되는 시간, n/1000초
            va.setDuration(600);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    //value는 height값
                    int value = (int)animation.getAnimatedValue();
                    //imageView의 높이 변경
                    item_detail.getLayoutParams().height = value;
                    item_detail.requestLayout();
                    //imageView가 실제로 사라지게하는 부분
                    item_detail.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                }
            });
            //Animator start
            va.start();
        }
    }
}
