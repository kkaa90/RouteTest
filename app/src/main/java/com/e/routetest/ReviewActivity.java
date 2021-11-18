package com.e.routetest;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        RecyclerView recyclerView = findViewById(R.id.RecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        ReviewAdapter adapter = new ReviewAdapter();
        recyclerView.setAdapter(adapter);

        getData(adapter);

        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(adapter);

    }

    private void getData(ReviewAdapter adapter) {
        List<String> listTitle = Arrays.asList("126078", "126079", "126080", "126081");
        List<String> listContent = Arrays.asList(
                "광안리해수욕장",
                "다대포해수욕장",
                "송정해수욕장",
                "해운대해수욕장"
        );

        for (int i = 0; i < listTitle.size(); i++) {
            ReviewData data = new ReviewData();
            data.setTitle(listTitle.get(i));
            data.setContent(listContent.get(i));

            adapter.addItem(data);
        }

        adapter.notifyDataSetChanged();
    }



}