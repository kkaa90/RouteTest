package com.e.routetest;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.Holder>{
    private Context context;
    private List<Board> boardList = new ArrayList<>();

    public BoardAdapter(Context context, List<Board> boardList) {
        this.context = context;
        this.boardList = boardList;
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_rv_item,parent,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        int itemPosition=position;
        holder.pfImg.setImageResource(R.drawable.photo1);
        System.out.println(boardList.get(itemPosition).boardTitle);
        System.out.println(itemPosition);
        String temp = boardList.get(itemPosition).boardTitle;
        holder.titleView.setText(temp);
        holder.themeView.setText(String.valueOf(boardList.get(itemPosition).themeID));
        holder.dateView.setText(boardList.get(itemPosition).boardDate);
        holder.nickView.setText(boardList.get(itemPosition).nickName);
        holder.desView.setText(boardList.get(itemPosition).destiny);
        holder.arrView.setText(boardList.get(itemPosition).arrival);
        holder.currView.setText(String.valueOf(boardList.get(itemPosition).currentP));
        holder.maxView.setText(String.valueOf(boardList.get(itemPosition).maxP));
    }

    @Override
    public int getItemCount() {

        return boardList.size();
    }
    public class Holder extends RecyclerView.ViewHolder{
        public ImageView pfImg;
        public TextView titleView;
        public TextView themeView;
        public TextView dateView;
        public TextView nickView;
        public TextView desView;
        public TextView arrView;
        public TextView currView;
        public TextView maxView;
        public Holder(View view){
            super(view);
            pfImg=(ImageView)view.findViewById(R.id.pfImg);
            titleView = (TextView)view.findViewById(R.id.titleText);
            themeView = (TextView)view.findViewById(R.id.themeT);
            dateView = (TextView)view.findViewById(R.id.dateRoute);
            nickView=(TextView)view.findViewById(R.id.nickName);
            desView=(TextView)view.findViewById(R.id.destiny);
            arrView=(TextView)view.findViewById(R.id.arrival);
            currView=(TextView)view.findViewById(R.id.currP);
            maxView=(TextView)view.findViewById(R.id.maxP);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos!= RecyclerView.NO_POSITION){
                        Intent intent = new Intent(view.getContext(),ShowBoardActivity.class);
                        intent.putExtra("1",boardList.get(pos).getBoardID());
                        view.getContext().startActivity(intent);
                    }
                }
            });
        }
    }
}
