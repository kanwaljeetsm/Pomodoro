package com.kanwaljeetsm.pomodoro;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Context context = MainActivity.this;
    private ImageView appDrawLogo;
    private ConstraintLayout main;
    private TextView txtTimer;
    private Button btnStart, btnEnd;
    private EditText edtSessionName;
    private SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private boolean isDark;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private final BroadcastReceiver timerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TimerService.ACTION_TIMER_TICK.equals(action) || TimerService.ACTION_STATUS_RESPONSE.equals(action)) {
                long timeLeft = intent.getLongExtra(TimerService.EXTRA_TIME_LEFT, 0);
                String type = intent.getStringExtra(TimerService.EXTRA_TIMER_TYPE);
                boolean isRunning = intent.getBooleanExtra(TimerService.EXTRA_IS_RUNNING, false);
                if (isRunning) {
                    updateUI(timeLeft, type);
                } else {
                    resetUI();
                }
            } else if (TimerService.ACTION_TIMER_FINISHED.equals(action)) {
                resetUI();
            }
        }
    };

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

        sharedPref = getSharedPreferences("SessionName", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        if (isMyServiceRunning(TimerService.class)) {
            btnStart.setVisibility(GONE);
            btnEnd.setVisibility(VISIBLE);
        }

        if (btnEnd.getVisibility() == VISIBLE) {
            edtSessionName.setText(sharedPref.getString("session_name", ""));
        }

        themeSettings();
        txtTimer.setText("25:00");

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionAndStart();
            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTimerService();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void checkPermissionAndStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            } else {
                startTimerService();
            }
        } else {
            startTimerService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTimerService();
            } else {
                Toast.makeText(this, "Notification permission is required for the timer to work in background", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startTimerService() {
        Intent intent = new Intent(this, TimerService.class);
        intent.setAction("START_WORK");
        intent.putExtra("SESSION_NAME", edtSessionName.getText().toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        btnStart.setVisibility(GONE);
        btnEnd.setVisibility(VISIBLE);
        edtSessionName.setEnabled(false);
    }

    private void stopTimerService() {
        Intent intent = new Intent(this, TimerService.class);
        intent.setAction("STOP_SERVICE");
        startService(intent);
        resetUI();
    }

    private void updateUI(long timeLeft, String type) {
        txtTimer.setText(String.format(Locale.US, "%02d:%02d", timeLeft / 60000, (timeLeft % 60000) / 1000));
        if (TimerService.TIMER_TYPE_WORK.equals(type)) {
            txtTimer.setTextColor(getResources().getColor(R.color.blue));
        } else {
            txtTimer.setTextColor(getResources().getColor(R.color.green));
        }
        btnStart.setVisibility(GONE);
        btnEnd.setVisibility(VISIBLE);
    }

    private void resetUI() {
        txtTimer.setText("25:00");
        txtTimer.setTextColor(isDark ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black));
        btnStart.setVisibility(VISIBLE);
        btnEnd.setVisibility(GONE);
        edtSessionName.setEnabled(true);
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TimerService.ACTION_TIMER_TICK);
        filter.addAction(TimerService.ACTION_TIMER_FINISHED);
        filter.addAction(TimerService.ACTION_STATUS_RESPONSE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(timerReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(timerReceiver, filter);
        }

        // Request status update from service after registering receiver to sync UI
        Intent intent = new Intent(this, TimerService.class);
        intent.setAction(TimerService.ACTION_REQUEST_STATUS);
        startService(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(timerReceiver);
        } catch (Exception e) {
            // Receiver not registered
        }
    }

    private void themeSettings() {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        isDark = nightModeFlags == Configuration.UI_MODE_NIGHT_YES;

        if (isDark) {
            appDrawLogo.setImageDrawable(getDrawable(R.drawable.drawpomoblack));
            main.setBackgroundColor(getColor(R.color.blackbg));
        } else {
            appDrawLogo.setImageDrawable(getDrawable(R.drawable.drawpomo));
            main.setBackgroundColor(getColor(R.color.white));
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.iconActivityHistory) {
            Intent intent = new Intent(this, ActivityHistory.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(btnEnd.getVisibility() == VISIBLE) {
            editor.putString("session_name", edtSessionName.getText().toString());
            editor.apply();
            Toast.makeText(MainActivity.this, "Session name saved", Toast.LENGTH_SHORT).show();
        }
    }
}