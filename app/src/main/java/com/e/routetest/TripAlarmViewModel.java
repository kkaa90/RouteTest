package com.e.routetest;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class TripAlarmViewModel extends ViewModel {
    public MutableLiveData<ArrayList<TripAlarm_rv_item_info>> itemLiveData = new MutableLiveData<ArrayList<TripAlarm_rv_item_info>>();

    public MutableLiveData<ArrayList<TripAlarm_rv_item_info>> getItemLiveData(){
        return itemLiveData;
    }


}
