package com.e.routetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private HomeFragment homeFragment = new HomeFragment();
    private RouteFragment routeFragment = new RouteFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,homeFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        BottomNavigationView navigationView = findViewById(R.id.main_navigation);
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.page_home:
                        FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction1.replace(R.id.main_frame,homeFragment);
                        fragmentTransaction1.addToBackStack(null);
                        fragmentTransaction1.commit();
                        return true;
                    case R.id.page_route:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,routeFragment).commit();
                        return true;

                }
                return false;
            }
        });

    }
}