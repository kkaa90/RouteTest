package com.e.routetest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.e.routetest.StartActivity.sv;

public class WriteBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_board);
        TextInputEditText tEdit = (TextInputEditText)findViewById(R.id.titleEdit);
        TextInputEditText wEdit = (TextInputEditText)findViewById(R.id.writerEdit);
        TextInputEditText rEdit = (TextInputEditText)findViewById(R.id.routeNumEdit);
        TextInputEditText cEdit = (TextInputEditText)findViewById(R.id.contentEdit);
        TextInputEditText lEdit = (TextInputEditText)findViewById(R.id.linkEdit);

        Button writeB = (Button)findViewById(R.id.participation);
        writeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    public void run(){
                        writeBoard(tEdit.getText().toString(),wEdit.getText().toString(),rEdit.getText().toString(),cEdit.getText().toString(),lEdit.getText().toString());
                    }
                }.start();
            }
        });




    }
    private void writeBoard(String t, String w, String r, String c, String l){
        try {
            String url = sv + "writeBoard.jsp?routeID="+r+"&userID="+w
                    +"&maxP=4&appliT=2021-10-20&boardTitle="+t+"&boardContent="+c+"&kakaoLink="+l;
            System.out.println(url);


            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(url).get();
            Request request = builder.build();

            Response response = client.newCall(request).execute();
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(response.body().string());
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String success = jsonObject.get("success").getAsString();
            System.out.println(success);

            if(success=="true"){ finish();}
            else {
                //new AlertDialog.Builder(WriteBoardActivity.this).setMessage("작성 실패").show();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

}