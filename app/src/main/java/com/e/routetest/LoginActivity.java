package com.e.routetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.e.routetest.LoadingActivity.sv;

public class LoginActivity extends AppCompatActivity {
    public static String nN = "temp";
    public static String gender = "성별";
    public static int age =0;
    public static String userId = "temp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText iEditText = (EditText)findViewById(R.id.id);
        EditText pEditText = (EditText)findViewById(R.id.pw);
        Button loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    public void run(){
                        userId=iEditText.getText().toString();
                        String s = signIn(userId,pEditText.getText().toString());
                        if(s.equals("true")){
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            intent.putExtra("USER_ID",userId);
                            startActivity(intent);
                            finish();
                        }
                        else {

                        }
                    }
                }.start();

                //System.out.println(idTextView.getText());
            }
        });
        Button signUpButton = (Button)findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    public String signIn(String id, String pwd) {
        String success="";
        try {
            String url = sv + "signIn.jsp?userID="+id+"&pwd="+pwd;
            System.out.println(url);


            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(url).get();
            Request request = builder.build();

            Response response = client.newCall(request).execute();
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(response.body().string());
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            success = jsonObject.get("success").getAsString();
            System.out.println(success);
            String msg = jsonObject.get("msg").getAsString();
            System.out.println(msg);
            nN = jsonObject.get("nickName").getAsString();
            if(jsonObject.get("gender").getAsString().equals("0")) {
                gender = "남자";
            }
            else
            {
                gender = "여자";
            }
            age = jsonObject.get("age").getAsInt();
            System.out.println("nickName : "+nN+", gender : "+gender+", age : "+age);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }
}