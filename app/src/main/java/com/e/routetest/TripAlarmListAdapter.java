package com.e.routetest;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.e.routetest.TripAlarmListActivity.tContext;

public class TripAlarmListAdapter extends RecyclerView.Adapter<TripAlarmListAdapter.Holder>{
    private Context context;
    private ArrayList<TripAlarm_rv_item_info> tripAlarmDataList;    //여행경로 데이터
    private ArrayList<Boolean> isVisitList; //여행지 방문 정보

    //아이템 확대 축소에 사용되는 변수들
    //item의 클릭 상태를 저장할 array 객체
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    //직전에 클릭됐던 item의 position
    private int prePosition = -1;

    public TripAlarmListAdapter(Context context, ArrayList<TripAlarm_rv_item_info> tripAlarmDataList, ArrayList<Boolean> isVisitList){
        this.context = context;
        this.tripAlarmDataList = tripAlarmDataList;
        this.isVisitList = isVisitList;
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
        public TextView serialNo;

        public ImageView weatherIcon;
        public TextView placeName;
        public TextView placeTmp;
        public TextView placeHum;
        public TextView placeRainfallProb;
        public TextView placeRainfallInfo;

        public ImageView timeIcon;
        public TextView placeArrivalTime;
        public TextView placeMoveTime;

        public ImageView changeButton;
        public ConstraintLayout item_detail;
        public LinearLayout item_info;

        private TripAlarm_rv_item_info tripAlarm_rv_item_info;
        private int position;

        //Holder 생성자
        public Holder(@NonNull View view) {
            super(view);
            serialNo = (TextView)view.findViewById(R.id.trip_alarm_rv_item_serial);

            weatherIcon = (ImageView)view.findViewById(R.id.trip_alarm_rv_item_weatherIcon);
            placeName = (TextView)view.findViewById(R.id.trip_alarm_rv_item_placeName);
            placeTmp = (TextView)view.findViewById(R.id.trip_alarm_rv_item_temperauterInfo);
            placeHum = (TextView)view.findViewById(R.id.trip_alarm_rv_item_HumidityInfo);
            placeRainfallProb = (TextView)view.findViewById(R.id.trip_alarm_rv_item_rainfallProbablityInfo);
            placeRainfallInfo = (TextView)view.findViewById(R.id.trip_alarm_rv_item_rainfallInfo);

            timeIcon = (ImageView)view.findViewById(R.id.trip_alarm_rv_item_timeIcon);
            placeArrivalTime = (TextView)view.findViewById(R.id.trip_alarm_rv_item_arrivalTime);
            placeMoveTime = (TextView) view.findViewById(R.id.trip_alarm_rv_item_moveTimeInfo);

            item_detail = (ConstraintLayout)view.findViewById(R.id.trip_alarm_rv_item_detail);
            item_info = (LinearLayout)view.findViewById(R.id.trip_alarm_rv_item_infoList);
            changeButton = (ImageView)view.findViewById(R.id.trip_alarm_rv_item_changeButton);
        }

        //onBindViewHolder에서 bind시켜주는 함수
        void onBind(TripAlarm_rv_item_info tripAlarm_rv_item_info, int position){
            this.tripAlarm_rv_item_info = tripAlarm_rv_item_info;
            this.position = position;
            TripAlarmComponent component = new TripAlarmComponent();

            //시리얼
            serialNo.setText("No."+component.makeSerial(position)+"  ");

            //날씨아이콘
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
            //장소이름
            placeName.setText(tripAlarm_rv_item_info.getPlaceName());
            placeName.setSingleLine(true);
            placeName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            placeName.setSelected(true);
            //온도
            String temperature = tripAlarm_rv_item_info.getPlaceTmp();
            temperature = temperature.equals("null")?"-":temperature+"°C";
            placeTmp.setText(temperature);
            //습도
            String humidity = tripAlarm_rv_item_info.getPlaceHum();
            humidity = humidity.equals("null")?"-":humidity+"%";
            placeHum.setText(humidity);
            //강수확률
            String rainProb = tripAlarm_rv_item_info.getPlaceRainfallProb();
            rainProb = rainProb.equals("null")?"-":rainProb+"%";
            placeRainfallProb.setText(rainProb);
            //강수or적설량
            String rsTmp1 = tripAlarm_rv_item_info.getPlaceRainfallInfo();
            if(rsTmp1.equals("null")) {
                placeRainfallInfo.setText("-");
            }else if(rsTmp1.equals("강수량 : 1.0mm 미만")){
                placeRainfallInfo.setText("-");
            }else{
                String[] rsTmp2 = rsTmp1.split(" ");
                String rainSnowInfo = rsTmp2[2];
                placeRainfallInfo.setText(rainSnowInfo);
            }

            //도착시간 출력
            String a_time = tripAlarm_rv_item_info.getArrivalTime();
            String at_h = a_time.substring(0,2);
            String at_m = a_time.substring(2);
            placeArrivalTime.setText(at_h+":"+at_m);

            //출발해야될 최소 시간 출력
            int lastIndex = tripAlarmDataList.size()-1;
            String m_time = tripAlarm_rv_item_info.getMoveTime();
            if(m_time.equals("null")){
                if(position == lastIndex) {
                    placeMoveTime.setText("마지막 여행지입니다.");
                }else {
                    placeMoveTime.setText("시간정보를 받아오지 못하였습니다.");
                }
            }else {
                String mt_h = m_time.substring(0, 2);
                String mt_m = m_time.substring(2);
                placeMoveTime.setText(mt_h + "시 " + mt_m + "분에 출발해야 시간내에 도착이 예상됩니다.");
            }
            Log.d("ADAPTER_REMAIN_TIME",""+tripAlarm_rv_item_info.getRemainTime());

            //남은시간
            if(tripAlarm_rv_item_info.getRemainTime().equals("null")){
                timeIcon.setImageResource(R.drawable.time4_g);
            }
            else{
                int remainTime = Integer.parseInt(tripAlarm_rv_item_info.getRemainTime()) - component.STANDARD_SPARE_TIME_MINUTE;
                int conv_remainTime = (remainTime%3600)/60;
                if(conv_remainTime>30){
                    timeIcon.setImageResource(R.drawable.time4_g);
                }else if(conv_remainTime>20&&conv_remainTime<=30){
                    timeIcon.setImageResource(R.drawable.time3);
                }else if(conv_remainTime>10&&conv_remainTime<=20){
                    timeIcon.setImageResource(R.drawable.time2);
                }else if(conv_remainTime>0&&conv_remainTime<=10){
                    timeIcon.setImageResource(R.drawable.time1);
                }else if(conv_remainTime<=0){
                    timeIcon.setImageResource(R.drawable.time0);
                }
            }


            changeVisibility(selectedItems.get(position));

            item_info.setOnClickListener(this);
            changeButton.setOnClickListener(this);
            //visitButton.setOnClickListener(this);
            //testButton.setOnClickListener(this);
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
                    builder.setPositiveButton("예",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //함수테스트
                            //Spot testSpot = new Spot(126078,"광안리해수욕장",129.1184922375,35.1537908369,"부산광역시 수영구 광안해변로 219");
                            //int testArrivalTime = 54000;
                            //new TripAlarmComponent().updateTRouteByIndex(context,position,testSpot,testArrivalTime);
                           String s_spotID = tripAlarm_rv_item_info.getPlaceID();
                           String s_routeID = tripAlarm_rv_item_info.getServerID();
                           Log.d("ADAPTER",s_spotID);
                           Log.d("ADAPTER",s_routeID);
                            if(s_spotID!=null && s_routeID!=null){
                                Intent intent = new Intent(((TripAlarmListActivity)tContext),RecommendActivity.class);
                                intent.putExtra("spotId",Integer.parseInt(s_spotID));
                                intent.putExtra("routeId",Integer.parseInt(s_routeID));
                                intent.putExtra("now",position);
                                ((TripAlarmListActivity)tContext).startActivityForResult(intent,10);
                            }
                            else{
                                Log.e("CHANGEBUTTON_ERROR","SPOTID or ROUTEID MISSING");
                            }
                        }
                    });
                    builder.setNegativeButton("아니오",null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                    break;
                /*
                case R.id.trip_alarm_rv_item_visitButton:
                    isVisitList.set(position,true);
                    item_detail.setBackgroundColor(Color.RED);;
                    break;

                case R.id.trip_alarm_rv_item_testButton:
                    Log.d("ISVISIT?",isVisitList.get(position)?"T":"F");
                    break;
                 */
            }
        }

        //view 확장을 위한 메소드
        private void changeVisibility(final boolean isExpanded){
            //height 값을 dp로 지정해서 넣고싶으면 아래 소스를 이용
            int dpValue = 110;
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
