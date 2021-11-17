package com.e.routetest;

import java.util.ArrayList;

public class TripAlarm_rv_ViewModel {
    private final ArrayList<TripAlarm_rv_item_info> items = new ArrayList<>();
    private final ArrayList<Boolean> visitList = new ArrayList<>();

    public ArrayList<TripAlarm_rv_item_info> getItems(){return this.items;}

    public ArrayList<Boolean> getVisitList(){return this.visitList;}
}
