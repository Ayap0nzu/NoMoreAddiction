package com.example.nomoreaddiction;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Chronometer;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{
    private static Chronometer chronometer;
    private long pauseOffset = 0;
    private boolean running;
    ArrayList<String> dateArray = new ArrayList<>();
    ArrayList<Long> chronometerArray = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd (EEE)", Locale.KOREA);
        String formattedDate = sdf.format(new Date());
        TextView textView = (TextView) findViewById(R.id.DATE);
        textView.setText(formattedDate);

        //      스탑워치
        chronometer = findViewById(R.id.chronometer);

        //      휴대폰을 얼마나 사용했는지 보여주는 알림 창

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        //      앱을 처음 실행 시켰을때 유저에게 앱 소개.
        final String PREFS_NAME = "MyPrefsFile";
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            Log.d("Comments", "First time");
            // first time task
            Intent intent = new Intent(this, firstexecute.class);
            startActivity(intent);
            // record the fact that the app has been started at least once
            settings.edit().putBoolean("my_first_time", false).apply();
        }
    }

    //onResume 함수 지우면 스탑워치가 안돌아가니 안지우게 주의
    private BroadcastReceiver screenOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //  만약 휴대폰 스크린이 꺼지면 스탑워치를 멈춘다.
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                chronometer.stop();
                pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
                running = false;
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        long chronometerTime = SystemClock.elapsedRealtime() - chronometer.getBase();
        // Save the current chronometer time to SharedPreferences
        SharedPreferences prefs = getSharedPreferences("mySharedPrefsFilename", Context.MODE_PRIVATE);

        //      Don't forget to call apply()
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("chronoValue", chronometerTime);

        String currentDate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
        editor.putString("currentDate", currentDate);

        // Get the current date

        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("mySharedPrefsFilename", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        long chronometerTime = prefs.getLong("chronoValue", 0);
        String storedDate = prefs.getString("currentDate", "");

        // Get the current date
        String currentDate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());

        //      sharedpreference 파일에 마지막으로 저장된 날짜와 현재 날짜를 비교해 다르면
        //      즉, 하루가 지났으면 스톱워치를 저장한다.
        if (!storedDate.equals(currentDate)) {
            chronometer.setBase(SystemClock.elapsedRealtime());
            editor.putLong("chronoValue", chronometerTime);
            editor.apply();
        }

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenOffReceiver, filter);

        //      알림창 관리
        if (!running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - chronometerTime);
            chronometer.start();
            running = true;

            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    long currentTime = SystemClock.elapsedRealtime() - chronometer.getBase();
                    int elapsedHours = (int) (currentTime / 3600000);
                    int elapsedMinutes = (int) ((currentTime / 60000) % 60);
                    int elapsedSeconds = (int) ((currentTime / 1000) % 60);

                    // Format the elapsed time as a string in "HH:MM:SS" format
                    String formattedTime = String.format("%02d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "My Notification");
                    builder.setContentTitle("휴대폰 중독 방지");
                    builder.setContentText("휴대폰 사용 시간: "+formattedTime);
                    builder.setSmallIcon(R.drawable.ic_launcher_background);
                    builder.setAutoCancel(false);
                    builder.setOngoing(true);

                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                    managerCompat.notify(1, builder.build());

                    // Schedule the next update after 5 seconds
                    handler.postDelayed(this, 5000);
                }
            };
            // Schedule the first update after 5 seconds
            handler.postDelayed(runnable, 5000);
        }
    }
}


