package com.e.routetest;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {
    private List<Notify> notifyList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_notification, container, false);
        RecyclerView recyclerView;
        NotificationAdapter notificationAdapter;
        notifyList = new ArrayList<>();
        recyclerView = (RecyclerView)view.findViewById(R.id.nRV);
        notificationAdapter = new NotificationAdapter(view.getContext(),notifyList);
        NotifyAppDatabase nDb = NotifyAppDatabase.getInstance(getContext());    //DB객체생성

        new Thread(){
            public void run(){
                notifyList.addAll(nDb.notifyRepository().findAll());        //찾기
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notificationAdapter.notifyDataSetChanged();
                    }
                });
            }
        }.start();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager lM = new LinearLayoutManager(getActivity().getApplicationContext());
        lM.setReverseLayout(true);
        lM.setStackFromEnd(true);
        recyclerView.setLayoutManager(lM);

        recyclerView.setAdapter(notificationAdapter);
        return view;
    }
}