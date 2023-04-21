package com.example.nomoreaddiction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class HistoryView extends AppCompatActivity {
    private TextView date;
    private TextView chronoMeterHistory;
    private Button button;
    private Button button2;
    private List<HistoryEntry> historyList;
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_view);
        chronoMeterHistory = (TextView) findViewById(R.id.chronometerHistory);
        date = (TextView) findViewById(R.id.dateHistory);
        button = (Button) findViewById(R.id.previousbtn);
        button2 = (Button) findViewById(R.id.nextbtn);

        SharedPreferences prefs = getSharedPreferences("mySharedPrefsFilename", Context.MODE_PRIVATE);
        String historyJson = prefs.getString("history", "");

        if (!historyJson.isEmpty()) {
            Type historyType = new TypeToken<List<HistoryEntry>>() {}.getType();
            historyList = new Gson().fromJson(historyJson, historyType);
            currentIndex = historyList.size() - 1;
            displayHistoryEntry(currentIndex);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex > 0) {
                    currentIndex--;
                    displayHistoryEntry(currentIndex);
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex < historyList.size() - 1) {
                    currentIndex++;
                    displayHistoryEntry(currentIndex);
                }
            }
        });
    }

    private void displayHistoryEntry(int index) {
        HistoryEntry entry = historyList.get(index);
        long chronometer = entry.getChronometerTime();
        int elapsedHours = (int) (chronometer / 3600000);
        int elapsedMinutes = (int) ((chronometer/ 60000) % 60);
        int elapsedSeconds = (int) ((chronometer / 1000) % 60);
        String formattedTime = String.format("%02d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
        chronoMeterHistory.setText(formattedTime);
        date.setText(entry.getStoredDate());
    }
}