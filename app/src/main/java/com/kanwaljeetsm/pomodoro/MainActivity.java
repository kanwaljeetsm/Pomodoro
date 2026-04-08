package com.kanwaljeetsm.pomodoro;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Context;
import android.content.res.Configuration;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;

public class MainActivity extends AppCompatActivity {

    private Context context = MainActivity.this;
    private ImageView appDrawLogo;
    private ConstraintLayout main;
    private TextView txtTimer;
    private Button btnStart, btnEnd;
    private EditText edtSessionName;
    private long timeLeftInMillis = 60000*25;
    private final long WORKTIME = (60000*1);
    private final long BREAKTIME = 60000*5;
    private CountDownTimer countDownTimerWork, countDownTimerBreak;


    private boolean isDark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


            appDrawLogo = findViewById(R.id.appDrawLogo);
            txtTimer = findViewById(R.id.txtTimer);
            main = findViewById(R.id.main);
            btnStart = findViewById(R.id.btnStart);
            btnEnd = findViewById(R.id.btnEnd);
            edtSessionName = findViewById(R.id.edtSessionName);

            themeSettings();
            txtTimer.setText("25:00");

            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startWorkTimer();
                }
            });

            btnEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    endSession();
                }
            });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void startWorkTimer() {
        ExecutorService executor = AppExecutor.getExecutorService();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // To update the UI from a background thread, use a Handler or runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnStart.setVisibility(GONE);
                        btnEnd.setVisibility(VISIBLE);
                        txtTimer.setTextColor(getResources().getColor(R.color.blue));
                        countDownTimerWork = new CountDownTimer(WORKTIME, 1000) {
                            @Override
                            public void onFinish() {
                                timeLeftInMillis = BREAKTIME;
                                txtTimer.setText(String.format(Locale.US,"%02d",timeLeftInMillis/60000) + ":" + String.format(Locale.US,"%02d",timeLeftInMillis%60000/1000));
                                startBreakTimer();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    HistoryStorage histObj = new HistoryStorage();
                                    DataActivityHistory historyData = new DataActivityHistory();
                                    historyData.setActivityDate(LocalDate.now());
                                    historyData.setStartTime(LocalTime.now().minusMinutes(25));
                                    historyData.setEndTime(LocalTime.now());
                                        if(edtSessionName.getText().toString().isBlank()) {
                                            historyData.setNotes(getResources().getString(R.string.strDefault));
                                        } else {
                                            historyData.setNotes(edtSessionName.getText().toString());
                                        }
                                    histObj.saveHistory(context, historyData);
                                }
                            }

                            @Override
                            public void onTick(long l) {
                                timeLeftInMillis = l;
                                txtTimer.setText(String.format(Locale.US,"%02d",timeLeftInMillis/60000) + ":" + String.format(Locale.US,"%02d",timeLeftInMillis%60000/1000));
                            }
                        }.start();
                    }
                });
            }
        });
    }

    private void startBreakTimer() {
        ExecutorService executor = AppExecutor.getExecutorService();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // To update the UI from a background thread, use a Handler or runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnEnd.setVisibility(GONE);
                        timeLeftInMillis = BREAKTIME;
                        txtTimer.setTextColor(getResources().getColor(R.color.green));
                        countDownTimerBreak = new CountDownTimer(BREAKTIME, 1000) {
                            @Override
                            public void onFinish() {
                                btnStart.setVisibility(VISIBLE);
                                txtTimer.setTextColor(getResources().getColor(R.color.blue));
                                timeLeftInMillis = WORKTIME;
                                txtTimer.setText(String.format(Locale.US,"%02d",timeLeftInMillis/60000) + ":" + String.format(Locale.US,"%02d",timeLeftInMillis%60000/1000));
                            }

                            @Override
                            public void onTick(long l) {
                                timeLeftInMillis = l;
                                txtTimer.setText(String.format(Locale.US,"%02d",timeLeftInMillis/60000) + ":" + String.format(Locale.US,"%02d",timeLeftInMillis%60000/1000));
                            }
                        }.start();
                    }
                });
            }
        });
    }

    private void endSession() {
        countDownTimerWork.cancel();
        txtTimer.setText("25:00");
        btnEnd.setVisibility(GONE);
        btnStart.setVisibility(VISIBLE);
    }

    private void themeSettings() {

        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        isDark = nightModeFlags == Configuration.UI_MODE_NIGHT_YES;

        if(isDark) {
            appDrawLogo.setImageDrawable(getDrawable(R.drawable.drawpomoblack));
            main.setBackgroundColor(getColor(R.color.black));
        } else {
            appDrawLogo.setImageDrawable(getDrawable(R.drawable.drawpomo));
            main.setBackgroundColor(getColor(R.color.white));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu); // Inflates your XML menu
        return true; // Return true to display the menu
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        if (item.getItemId() == R.id.iconActivityHistory) {
            Intent intent = new Intent(this, ActivityHistory.class);
            startActivity(intent);
            return true;
        }
        return true;
    }
}