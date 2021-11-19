package com.e.routetest;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static com.e.routetest.LoadingActivity.sv;
import static com.e.routetest.O2MActivity.oContext;
import static com.e.routetest.TestActivity2.mContext;

public class O2MAdapter extends RecyclerView.Adapter<O2MAdapter.Holder> {
    private Context context;
    private List<O2MRequest> o2MRequests = new ArrayList<>();
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private int prePosition = -1;

    public O2MAdapter(Context context, List<O2MRequest> o2MRequests) {
        this.context = context;
        this.o2MRequests = o2MRequests;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.o2m_rv_item,parent,false);
        O2MAdapter.Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        int itemPosition=position;
        holder.titleView.setText(o2MRequests.get(itemPosition).boardTitle);
        holder.idView.setText(o2MRequests.get(itemPosition).userID);
        holder.nNView.setText(o2MRequests.get(itemPosition).nickName);
        holder.genderView.setText(o2MRequests.get(itemPosition).gender);
        holder.ageView.setText(Integer.toString(o2MRequests.get(itemPosition).age));
        holder.spot1View.setText(o2MRequests.get(itemPosition).spot1);
        holder.spot2View.setText(o2MRequests.get(itemPosition).spot2);
        holder.dateView.setText(o2MRequests.get(itemPosition).date);
        holder.goBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),ShowBoardActivity.class);
                intent.putExtra("1",o2MRequests.get(itemPosition).boardID);
                view.getContext().startActivity(intent);
            }
        });
        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    public void run(){
                        if(acceptGuest(o2MRequests.get(itemPosition).boardID,o2MRequests.get(itemPosition).userID,"true")){
                            AlertDialog.Builder builder = new AlertDialog.Builder((oContext));
                            builder.setTitle("수락 완료");
                            builder.setMessage("수락되었습니다.");
                            builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            android.os.Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    builder.create().show();
                                }
                            });
                        }
                        else{
                            AlertDialog.Builder builder = new AlertDialog.Builder((oContext));
                            builder.setTitle("오류");
                            builder.setMessage("오류가 발생하였습니다.");
                            builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            android.os.Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    builder.create().show();
                                }
                            });
                        }
                    }
                }.start();

            }
        });
        holder.rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    public void run(){
                        if(acceptGuest(o2MRequests.get(itemPosition).boardID,o2MRequests.get(itemPosition).userID,"false")){
                            AlertDialog.Builder builder = new AlertDialog.Builder((oContext));
                            builder.setTitle("거절");
                            builder.setMessage("거절되었습니다.");
                            builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            android.os.Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    builder.create().show();
                                }
                            });
                        }
                        else {
                            AlertDialog.Builder builder = new AlertDialog.Builder((oContext));
                            builder.setTitle("오류");
                            builder.setMessage("오류가 발생하였습니다.");
                            builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            android.os.Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    builder.create().show();
                                }
                            });

                        }
                    }
                }.start();

            }
        });
    }

    @Override
    public int getItemCount() {
        return o2MRequests.size();
    }


    public class Holder extends RecyclerView.ViewHolder{
        TextView titleView;
        TextView idView;
        TextView nNView;
        TextView genderView;
        TextView ageView;
        TextView spot1View;
        TextView spot2View;
        TextView dateView;
        Button goBoardButton;
        Button acceptButton;
        Button rejectButton;
        ConstraintLayout eC;
        TextView exView;
        public Holder(View view){
            super(view);
            titleView = (TextView)view.findViewById(R.id.o2mTitleView);
            idView = (TextView)view.findViewById(R.id.o2mIdView);
            nNView = (TextView)view.findViewById(R.id.o2mNNView);
            genderView = (TextView)view.findViewById(R.id.o2mGenderView);
            ageView = (TextView)view.findViewById(R.id.o2mAgeView);
            spot1View = (TextView)view.findViewById(R.id.o2mFSView);
            spot2View = (TextView)view.findViewById(R.id.o2mLSView);
            dateView = (TextView)view.findViewById(R.id.o2mDateView);
            goBoardButton = (Button)view.findViewById(R.id.o2MGoBoardButton);
            acceptButton = (Button)view.findViewById(R.id.o2mAcceptButton);
            rejectButton = (Button)view.findViewById(R.id.o2mRejectButton);
            eC = (ConstraintLayout)view.findViewById(R.id.o2mEC);
            exView = (TextView)view.findViewById(R.id.o2MEx);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(selectedItems.get(pos)){
                        selectedItems.delete(pos);
                        eC.setVisibility(View.GONE);
                        exView.setVisibility(View.VISIBLE);
                    }
                    else{
                        selectedItems.put(pos,true);
                        eC.setVisibility(View.VISIBLE);
                        exView.setVisibility(View.GONE);
                    }

                }
            });
        }

    }
    public boolean acceptGuest(int boardID, String ui, String acc) {

        try {
            String url = sv + "acceptGuest.jsp?boardID="+boardID+"&userID="+ui+"&accept="+acc;
            System.out.println(url);


            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(url).get();
            Request request = builder.build();

            Response response = client.newCall(request).execute();
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(response.body().string());
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String success = jsonObject.get("success").getAsString();
            System.out.println(success);
            String msg = jsonObject.get("msg").getAsString();
            System.out.println(msg);
            if(success.equals("true")) return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
