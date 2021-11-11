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

import static com.e.routetest.LoadingActivity.allSpotList;
import static com.e.routetest.LoadingActivity.allSpotName;
import static com.e.routetest.LoadingActivity.sv;

public class SearchFragment extends Fragment {

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
        for(int i=0;i<allSpotName.size();i++) {
            System.out.println(allSpotName.get(i));
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewSpotAdapter.getFilter().filter(searchText.getText());
            }
        }, 2000);
        return view;

    }


}