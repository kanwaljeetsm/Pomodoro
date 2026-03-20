package com.kanwaljeetsm.pomodoro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public class RecyclerAdapterActivityHistory extends RecyclerView.Adapter<RecyclerAdapterActivityHistory.ViewHolder> {

    List<String> lstNotes;
    List<LocalTime> lstStartTime;
    List<LocalTime> lstEndTime;
    List<LocalDate> lstActivityDate;
    Context context;

    public RecyclerAdapterActivityHistory(Context context, List<LocalDate> lstActivityDate, List<LocalTime> lstStartTime, List<LocalTime> lstEndTime, List<String> lstNotes) {
        this.context = context;
        this.lstActivityDate = lstActivityDate;
        this.lstEndTime = lstEndTime;
        this.lstStartTime = lstStartTime;
        this.lstNotes = lstNotes;
    }

    @NonNull
    @Override
    public RecyclerAdapterActivityHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 1. Use the parent's context for inflation
        // 2. Use a more descriptive variable name
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_history_recycler, parent, false);

        // 3. Instantiate YOUR specific ViewHolder class, not the abstract base class
        // 4. Return directly to reduce boilerplate
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterActivityHistory.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return lstStartTime.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
