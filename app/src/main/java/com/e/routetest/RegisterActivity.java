package com.e.routetest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.e.routetest.LoadingActivity.allSpotList;
import static com.e.routetest.LoadingActivity.sv;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Spinner gSpinner = (Spinner)findViewById(R.id.genderSpinner);
        ArrayAdapter gAdapter = ArrayAdapter.createFromResource(this,R.array.gender, android.R.layout.simple_spinner_dropdown_item);
        gSpinner.setAdapter(gAdapter);
        Spinner aSpinner = (Spinner)findViewById(R.id.ageSpinner);
        ArrayAdapter aAdapter = ArrayAdapter.createFromResource(this,R.array.age, android.R.layout.simple_spinner_dropdown_item);
        aSpinner.setAdapter(aAdapter);

        EditText nEditText = (EditText)findViewById(R.id.join_name);
        EditText iEditText = (EditText)findViewById(R.id.join_id);
        EditText pEditText = (EditText)findViewById(R.id.join_password);
        EditText pEditText2 = (EditText)findViewById(R.id.join_pwchk);
        Button jButton = (Button)findViewById(R.id.join_button);
        jButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    public void run(){
                        String g;
                        if(gSpinner.getSelectedItem().toString().equals("남자")){
                            g="1";
                        }
                        else {
                            g="2";
                        }

                        if(signUp(iEditText.getText().toString(),nEditText.getText().toString(), g,
                                pEditText.getText().toString(), aSpinner.getSelectedItem().toString())==1){
                            finish();
                        }
                        else {

                        }
                    }
                }.start();
            }
        });
        Button qButton = (Button)findViewById(R.id.delete);
        qButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
    public int signUp(String id, String nickName, String gender, String pwd, String age) {

        try {
            String url = sv + "signUp.jsp?userID="+id+"&nickName="+nickName+"&gender="+gender
                    +"&pwd="+pwd+"&age="+age;
            System.out.println(url);


            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(url).get();
            Request request = builder.build();

            Response response = client.newCall(request).execute();
            Gson gson = new GsonBuilder().setLenient().create();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(response.body().string());
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String success = jsonObject.get("success").getAsString();
            System.out.println(success);
            String msg = jsonObject.get("msg").getAsString();
            System.out.println(msg);
            if(success.equals("true")){
                return 1;
            }
            else {
                return 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}