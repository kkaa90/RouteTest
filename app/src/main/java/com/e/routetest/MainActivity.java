package com.e.routetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    public static String token = "";
    private static final String TAG = "MainActivity";
    private HomeFragment homeFragment = new HomeFragment();
    private RouteFragment routeFragment = new RouteFragment();
    private BoardFragment boardFragment = new BoardFragment();
    private NotificationFragment notificationFragment = new NotificationFragment();
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
                    case R.id.page_board:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,boardFragment).commit();
                        return true;
                    case R.id.page_notice:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,notificationFragment).commit();
                        return true;
                }
                return false;
            }
        });
        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(!task.isSuccessful()){
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        token = task.getResult();

                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                    }

                });

    }


}