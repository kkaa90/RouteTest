package com.e.routetest;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchFragment extends Fragment {
    public static ArrayList<Spot> allSpotList = new ArrayList<Spot>();
    ArrayList<String> allSpotName = new ArrayList<String>();
    public SearchFragment() {
        // Required empty public constructor
    }

    public void postToastMessage(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        RecyclerView recyclerView2 = view.findViewById(R.id.viewAllSpot);
        AutoCompleteTextView searchText = (AutoCompleteTextView)view.findViewById(R.id.editTextSearch);
        recyclerView2.setHasFixedSize(true);
        ViewSpotAdapter viewSpotAdapter = new ViewSpotAdapter(getActivity(), allSpotList);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView2.setAdapter(viewSpotAdapter);
        Button button = (Button) view.findViewById(R.id.buttonSearch);

        ArrayAdapter<String> autoAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, allSpotName);
        searchText.setAdapter(autoAdapter);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                viewSpotAdapter.getFilter().filter(searchText.getText());
            }

        });
        allSpotList.clear();
        new Thread() {
            public void run() {
                getSpot();
            }
        }.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewSpotAdapter.getFilter().filter(searchText.getText());
            }
        }, 2000);
        return view;

    }

    public int getSpot() {
        int spotId;
        String title;
        double x;
        double y;
        String address;
        int status = 0;
        try {
            String url = "http://13.125.252.236:8080/teamproject/viewAttraction.jsp";
            System.out.println(url);


            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(url).get();
            Request request = builder.build();

            Response response = client.newCall(request).execute();
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(response.body().string());
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            JsonArray row = jsonObject.get("attractions").getAsJsonArray();
            for (int i = 0; i < row.size(); i++) {
                JsonObject jsonObject1 = (JsonObject) row.get(i);
                spotId = jsonObject1.get("attractionID").getAsInt();
                title = jsonObject1.get("title").getAsString();
                x = jsonObject1.get("mapX").getAsDouble();
                y = jsonObject1.get("mapY").getAsDouble();
                address = jsonObject1.get("addr").getAsString();
                allSpotList.add(new Spot(spotId, title, y, x, address));
                allSpotName.add(title);
                //System.out.println(i);
            }
            postToastMessage("갱신 완료");
            status = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }
}