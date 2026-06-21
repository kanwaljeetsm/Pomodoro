package com.kanwaljeetsm.pomodoro;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;

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
import java.util.List;

public class ActivityHistory extends AppCompatActivity {

    private RecyclerAdapterActivityHistory recyclerAdapterActivityHistory;
    private RecyclerView recyclerActivityHistory;

    private List<String> lstNotes = new ArrayList<>();
    private List<LocalTime> lstStartTime = new ArrayList<>();
    private List<LocalTime> lstEndTime = new ArrayList<>();
    private List<LocalDate> lstActivityDate = new ArrayList<>();
    private List<DataActivityHistory> historyList = Collections.emptyList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        // Move RecyclerView initialization OUT of the insets listener.
        // The listener is triggered every time the keyboard appears/disappears.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Load data once
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            historyList = HistoryStorage.loadHistory(this);
        }

        // 2. Initialize RecyclerView once
        recyclerActivityHistory = findViewById(R.id.recyclerActivityHistory);
        recyclerActivityHistory.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        // 3. Setup Adapter and LayoutManager once
        recyclerAdapterActivityHistory = new RecyclerAdapterActivityHistory(this, lstActivityDate, lstStartTime, lstEndTime, lstNotes, historyList);
        recyclerActivityHistory.setAdapter(recyclerAdapterActivityHistory);
        recyclerActivityHistory.setLayoutManager(new LinearLayoutManager(this));
    }
}