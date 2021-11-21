package com.e.routetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import static com.e.routetest.LoginActivity.age;
import static com.e.routetest.LoginActivity.gender;
import static com.e.routetest.LoginActivity.nN;
import static com.e.routetest.LoginActivity.userId;

public class MainActivity extends AppCompatActivity {
    public static String token = "";
    private static final String TAG = "MainActivity";
    private HomeFragment homeFragment = new HomeFragment();
    private RouteFragment routeFragment = new RouteFragment();
    private BoardFragment boardFragment = new BoardFragment();
    private NotificationFragment notificationFragment = new NotificationFragment();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://route-f81c2-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
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
                        String msg2 = "환영합니다. "+nN+"님";
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this,msg2,Toast.LENGTH_SHORT).show();
                        databaseReference.child("users").child(userId).child("nickName").setValue(nN);
                        databaseReference.child("users").child(userId).child("gender").setValue(gender);
                        databaseReference.child("users").child(userId).child("age").setValue(age);
                        databaseReference.child("users").child(userId).child("token").setValue(token);
                        System.out.println("nickName : "+nN+" gender : "+gender+" age : " +age );
                    }
                });
    }
}