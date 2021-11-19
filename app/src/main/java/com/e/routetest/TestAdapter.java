package com.e.routetest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import android.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.e.routetest.TestActivity2.mContext;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.Holder> {
    private Context context;
    private List<Sp> list =new ArrayList<>();

    public TestAdapter(Context context, List<Sp> list){
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_rv_item,parent,false);
        TestAdapter.Holder holder = new TestAdapter.Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        int itemPosition=position;
        String sArr[] = list.get(position).getSpotsName().split(",");
        holder.spotName1.setText(sArr[0]);
        holder.spotName2.setText(sArr[sArr.length-1]);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        public TextView spotName1;
        public TextView spotName2;
        public Holder(View view){
            super(view);

            spotName1=(TextView) view.findViewById(R.id.testName1);
            spotName2=(TextView)view.findViewById(R.id.testName2);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos!= RecyclerView.NO_POSITION){
                        AlertDialog.Builder builder = new AlertDialog.Builder((mContext));
                        builder.setTitle("경로 선택");
                        builder.setMessage("선택완료");
                        builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent();
                                intent.putExtra("result",list.get(pos).getRouteId());
                                System.out.println("값 : "+list.get(pos).getRouteId());
                                ((TestActivity2)mContext).setResult(RESULT_OK,intent);
                                ((TestActivity2)mContext).finish();
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
            });

        }
    }
}
