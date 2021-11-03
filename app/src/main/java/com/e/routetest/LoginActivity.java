package com.e.routetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        RadioButton rB1 = (RadioButton)findViewById(R.id.idGuest1);
        RadioButton rB2 = (RadioButton)findViewById(R.id.idGuest2);
        RadioButton rB3 = (RadioButton)findViewById(R.id.idGuest3);
        RadioButton rB4 = (RadioButton)findViewById(R.id.idGuest4);
        RadioButton rB5 = (RadioButton)findViewById(R.id.idGuest5);

        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        Button loginButton = (Button)findViewById(R.id.loginButton);
        TextView idTextView = (TextView)findViewById(R.id.idTextView);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.idGuest1:
                        idTextView.setText("guest1");
                        break;
                    case R.id.idGuest2:
                        idTextView.setText("guest2");
                        break;
                    case R.id.idGuest3:
                        idTextView.setText("guest3");
                        break;
                    case R.id.idGuest4:
                        idTextView.setText("guest4");
                        break;
                    case R.id.idGuest5:
                        idTextView.setText("guest5");
                        break;

                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RouteActivity.class);
                intent.putExtra("2", idTextView.getText());
                startActivity(intent);
                //System.out.println(idTextView.getText());
            }
        });
    }
}