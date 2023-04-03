package com.example.nomoreaddiction;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Chronometer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{
    private static Chronometer chronometer;
    private long pauseOffset = 0;
    private boolean running;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd (EEE)", Locale.KOREA);
        String formattedDate = sdf.format(new Date());

        TextView textView = (TextView) findViewById(R.id.DATE);
        textView.setText(formattedDate);

        //스탑워치
        chronometer = findViewById(R.id.chronometer);
        //  앱을 처음 실행 시켰을때 유저에게 앱 소개.
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

    //onResume, onPause 함수 지우면 스탑워치가 안돌아가니 안지우게 주의

    @Override
    protected void onResume() {
        super.onResume();
        if(!running){
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(running){
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            Log.v("#@%@%#", "Power button is pressed.");
            Toast.makeText(arg0, "power button clicked",Toast.LENGTH_LONG).show();
            //perform what you want here
            if(!running){
                chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                chronometer.start();
                running = true;
            }
            if(running){
                chronometer.stop();
                pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
                running = false;
            }
        }
    }
}
