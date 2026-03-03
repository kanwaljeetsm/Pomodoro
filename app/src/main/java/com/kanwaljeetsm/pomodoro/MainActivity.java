package com.kanwaljeetsm.pomodoro;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;

public class MainActivity extends AppCompatActivity {

    private Context context = MainActivity.this;
    private ImageView appDrawLogo;
    private ConstraintLayout main;
    private TextView txtTimer;
    private Button btnStart;
    private long timeLeftInMillis = 60000*25;
    private final long WORKTIME = 60000*25;
    private final long BREAKTIME = 60000*5;


    private boolean isDark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            appDrawLogo = findViewById(R.id.appDrawLogo);
            txtTimer = findViewById(R.id.txtTimer);
            main = findViewById(R.id.main);
            btnStart = findViewById(R.id.btnStart);

            themeSettings();
            txtTimer.setText("25:00");

            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startWorkTimer();
                }
            });

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
                        btnStart.setEnabled(false);
                        txtTimer.setTextColor(getResources().getColor(R.color.blue));
                        CountDownTimer countDownTimer = new CountDownTimer(WORKTIME, 1000) {
                            @Override
                            public void onFinish() {
                                timeLeftInMillis = BREAKTIME;
                                txtTimer.setText(String.format(Locale.US,"%02d",timeLeftInMillis/60000) + ":" + String.format(Locale.US,"%02d",timeLeftInMillis%60000/1000));
                                startBreakTimer();
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
                        timeLeftInMillis = BREAKTIME;
                        txtTimer.setTextColor(getResources().getColor(R.color.green));
                        CountDownTimer countDownTimer = new CountDownTimer(BREAKTIME, 1000) {
                            @Override
                            public void onFinish() {
                                btnStart.setEnabled(true);
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
}