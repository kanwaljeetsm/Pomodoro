package com.kanwaljeetsm.pomodoro;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class RecyclerAdapterActivityHistory extends RecyclerView.Adapter<RecyclerAdapterActivityHistory.ViewHolder> {

    List<String> lstNotes;
    private List<DataActivityHistory> historyList;
    List<LocalTime> lstStartTime;
    List<LocalTime> lstEndTime;
    List<LocalDate> lstActivityDate;
    Context context;
    HistoryStorage histObj;

    public RecyclerAdapterActivityHistory(Context context, List<LocalDate> lstActivityDate, List<LocalTime> lstStartTime, List<LocalTime> lstEndTime, List<String> lstNotes, List<DataActivityHistory> historyList) {
        this.context = context;
        this.lstActivityDate = lstActivityDate;
        this.lstEndTime = lstEndTime;
        this.lstStartTime = lstStartTime;
        this.lstNotes = lstNotes;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public RecyclerAdapterActivityHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_history_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterActivityHistory.ViewHolder holder, int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DataActivityHistory item = historyList.get(position);
            
            // Optional: The following lines were in the original code but are likely redundant 
            // if historyList already contains the data. They are preserved for consistency 
            // with the original logic if they are used elsewhere.
            histObj = new HistoryStorage();
            histObj.loadHistory(context);
            lstActivityDate.add(item.getActivityDate());
            lstStartTime.add(item.getStartTime());
            lstEndTime.add(item.getEndTime());
            lstNotes.add(item.getNotes());

            holder.txtDate.setText(item.getActivityDate().toString());
            holder.txtStartTime.setText(item.getStartTime().toString().replaceAll("\\..*", ""));
            holder.txtEndTime.setText(item.getEndTime().toString().replaceAll("\\..*", ""));
            holder.txtNotes.setText(item.getNotes());
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate, txtStartTime, txtEndTime, txtNotes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtStartTime = itemView.findViewById(R.id.txtStartTime);
            txtEndTime = itemView.findViewById(R.id.txtEndTime);
            txtNotes = itemView.findViewById(R.id.txtNotes);
        }
    }
}
