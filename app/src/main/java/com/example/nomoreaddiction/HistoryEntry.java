package com.example.nomoreaddiction;

import android.widget.Button;

public class HistoryEntry {
    private long chronometerTime;
    private String storedDate;
    public HistoryEntry(long chronometerTime, String storedDate) {
        this.chronometerTime = chronometerTime;
        this.storedDate = storedDate;
    }

    public long getChronometerTime() {
        return chronometerTime;
    }

    public String getStoredDate() {
        return storedDate;
    }
}
