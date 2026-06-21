package com.kanwaljeetsm.pomodoro;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchUIUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
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
            holder.llNotesHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(holder.btnNotesSave.getVisibility() == View.GONE) {
                        holder.edtNotes.setVisibility(View.VISIBLE);
                        holder.btnNotesSave.setVisibility(View.VISIBLE);
                        holder.imgNotesDropdown.setImageDrawable(context.getResources().getDrawable(R.drawable.outline_arrow_drop_up_24));
                    } else if (holder.btnNotesSave.getVisibility() == View.VISIBLE) {
                        holder.edtNotes.setVisibility(View.GONE);
                        holder.btnNotesSave.setVisibility(View.GONE);
                        holder.imgNotesDropdown.setImageDrawable(context.getResources().getDrawable(R.drawable.outline_arrow_drop_down_24));
                        hideKeyboard(view);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate, txtStartTime, txtEndTime, txtNotes;
        LinearLayout llNotesHeader;
        EditText edtNotes;
        Button btnNotesSave;
        ImageButton imgNotesDropdown;
        RecyclerView recyclerView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtStartTime = itemView.findViewById(R.id.txtStartTime);
            txtEndTime = itemView.findViewById(R.id.txtEndTime);
            txtNotes = itemView.findViewById(R.id.txtNotes);
            llNotesHeader = itemView.findViewById(R.id.llNotesHeader);
            edtNotes = itemView.findViewById(R.id.edtNotes);
            btnNotesSave = itemView.findViewById(R.id.btnNotesSave);
            imgNotesDropdown = itemView.findViewById(R.id.imgNotesDropdown);
            recyclerView = itemView.findViewById(R.id.recyclerActivityHistory);
        }
    }
    public static void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}