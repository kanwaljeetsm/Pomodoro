package com.kanwaljeetsm.pomodoro;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;

public class TimerService extends Service {

    private static final String TAG = "TimerService";

    public static final String ACTION_TIMER_TICK = "com.kanwaljeetsm.pomodoro.TIMER_TICK";
    public static final String ACTION_TIMER_FINISHED = "com.kanwaljeetsm.pomodoro.TIMER_FINISHED";
    public static final String ACTION_REQUEST_STATUS = "com.kanwaljeetsm.pomodoro.REQUEST_STATUS";
    public static final String ACTION_STATUS_RESPONSE = "com.kanwaljeetsm.pomodoro.STATUS_RESPONSE";

    public static final String EXTRA_TIME_LEFT = "extra_time_left";
    public static final String EXTRA_TIMER_TYPE = "extra_timer_type";
    public static final String EXTRA_IS_RUNNING = "extra_is_running";

    public static final String TIMER_TYPE_WORK = "work";
    public static final String TIMER_TYPE_BREAK = "break";

    private static final String CHANNEL_ID = "TimerServiceChannel";
    private static final int NOTIFICATION_ID = 1;

    private CountDownTimer countDownTimer;
    private final long WORK_TIME = 25 * 60000;
    private final long BREAK_TIME = 25 * 60000;

    private long timeLeftInMillis = WORK_TIME;
    private String currentTimerType = TIMER_TYPE_WORK;
    private String sessionName = "";
    private boolean isTimerRunning = false;
    private MediaPlayer mediaPlayerSession, mediaPlayerBreak;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            Log.d(TAG, "onStartCommand: " + action);
            switch (action) {
                case "START_WORK":
                    sessionName = intent.getStringExtra("SESSION_NAME");
                    startWorkTimer();
                    break;
                case "STOP_SERVICE":
                    stopTimer();
                    stopForeground(true);
                    stopSelf();
                    break;
                case ACTION_REQUEST_STATUS:
                    sendStatusBroadcast(ACTION_STATUS_RESPONSE);
                    break;
            }
        }
        return START_STICKY;
    }

    private void startWorkTimer() {
        stopTimer();
        currentTimerType = TIMER_TYPE_WORK;
        timeLeftInMillis = WORK_TIME;
        isTimerRunning = true;
        mediaPlayerSession = MediaPlayer.create(this, R.raw.sessioncomplete);

        createNotificationChannel();
        startForeground(NOTIFICATION_ID, getNotification("Work Session Started"));
        sendStatusBroadcast(ACTION_TIMER_TICK);

        countDownTimer = new CountDownTimer(WORK_TIME, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMillis = l;
                sendStatusBroadcast(ACTION_TIMER_TICK);
                updateNotification("Work: " + formatTime(l));
            }

            @Override
            public void onFinish() {
                saveHistory();
                startBreakTimer();
                mediaPlayerSession.start();
            }
        }.start();
    }

    private void startBreakTimer() {
        stopTimer();
        currentTimerType = TIMER_TYPE_BREAK;
        timeLeftInMillis = BREAK_TIME;
        isTimerRunning = true;
        mediaPlayerBreak = MediaPlayer.create(this, R.raw.breakcomplete);

        startForeground(NOTIFICATION_ID, getNotification("Break Session Started"));
        sendStatusBroadcast(ACTION_TIMER_TICK);

        countDownTimer = new CountDownTimer(BREAK_TIME, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMillis = l;
                sendStatusBroadcast(ACTION_TIMER_TICK);
                updateNotification("Break: " + formatTime(l));
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                timeLeftInMillis = WORK_TIME;
                currentTimerType = TIMER_TYPE_WORK;
                Intent intent = new Intent(ACTION_TIMER_FINISHED);
                intent.setPackage(getPackageName());
                sendBroadcast(intent);
                stopForeground(true);
                stopSelf();
                mediaPlayerBreak.start();
            }
        }.start();
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
    }

    private void sendStatusBroadcast(String action) {
        Log.d(TAG, "Sending broadcast: " + action + " (isRunning=" + isTimerRunning + ")");
        Intent intent = new Intent(action);
        intent.setPackage(getPackageName());
        intent.putExtra(EXTRA_TIME_LEFT, timeLeftInMillis);
        intent.putExtra(EXTRA_TIMER_TYPE, currentTimerType);
        intent.putExtra(EXTRA_IS_RUNNING, isTimerRunning);
        sendBroadcast(intent);
    }

    private void saveHistory() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DataActivityHistory historyData = new DataActivityHistory();
            historyData.setActivityDate(LocalDate.now());
            historyData.setStartTime(LocalTime.now().minusMinutes(25));
            historyData.setEndTime(LocalTime.now());
            if (sessionName == null || sessionName.trim().isEmpty()) {
                historyData.setNotes(getString(R.string.strDefault));
            } else {
                historyData.setNotes(sessionName);
            }
            HistoryStorage.saveHistory(this, historyData);
        }
    }

    private String formatTime(long millis) {
        return String.format(Locale.US, "%02d:%02d", millis / 60000, (millis % 60000) / 1000);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Timer Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private Notification getNotification(String contentText) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Pomodoro Timer")
                .setContentText(contentText)
                .setSmallIcon(R.mipmap.pomo)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .build();
    }

    private void updateNotification(String contentText) {
        Notification notification = getNotification(contentText);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, notification);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopTimer();
        super.onDestroy();
    }
}