package com.e.routetest;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPageAdapter extends FragmentPagerAdapter {
    int num;

    public ViewPageAdapter(FragmentManager fm, int num){
        super(fm);
        this.num=num;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                SearchFragment f1=new SearchFragment();
                return f1;
            case 1:
                MapsFragment f2=new MapsFragment();
                return f2;
            case 2:
                RouteFragment f3 = new RouteFragment();
                return f3;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return num;
    }
}
