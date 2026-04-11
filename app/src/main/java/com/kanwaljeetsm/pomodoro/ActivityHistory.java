package com.kanwaljeetsm.pomodoro;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ActivityHistory extends AppCompatActivity {

    private RecyclerAdapterActivityHistory recyclerAdapterActivityHistory;
    RecyclerView recyclerActivityHistory;

    private List<String> lstNotes = new ArrayList<>();
    private List<LocalTime> lstStartTime = new ArrayList<>();
    private List<LocalTime> lstEndTime = new ArrayList<>();
    private List<LocalDate> lstActivityDate = new ArrayList<>();
    List<DataActivityHistory> historyList = Collections.emptyList();
    private Context context = ActivityHistory.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            // 1. Get the data from storage
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                historyList = HistoryStorage.loadHistory(this);
            }

            // 2. Initialize RecyclerView
            RecyclerView recyclerView = findViewById(R.id.recyclerActivityHistory);

            // 3. Create and set the Adapter
            RecyclerAdapterActivityHistory adapter = new RecyclerAdapterActivityHistory(this, lstActivityDate, lstStartTime, lstEndTime, lstNotes, historyList);
            recyclerView.setAdapter(adapter);

            // 4. Set the LayoutManager (Vertical list)
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter.notifyDataSetChanged();

            return insets;
        });



    }
}