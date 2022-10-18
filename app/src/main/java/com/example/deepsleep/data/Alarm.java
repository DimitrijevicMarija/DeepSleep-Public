package com.example.deepsleep.data;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Alarm {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long millis;
    private String time;
    private int hour;
    private int minute;
    private boolean valid;
    private float volume;
    private boolean morningTest;
    private boolean vibration;

    public Alarm(long id, long millis, String time, int hour, int minute, boolean valid, float volume, boolean morningTest, boolean vibration) {
        this.id = id;
        this.millis = millis;
        this.time = time;
        this.hour = hour;
        this.minute = minute;
        this.valid = valid;
        this.volume = volume;
        this.morningTest = morningTest;
        this.vibration = vibration;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public boolean isMorningTest() {
        return morningTest;
    }

    public void setMorningTest(boolean morningTest) {
        this.morningTest = morningTest;
    }

    public boolean isVibration() {
        return vibration;
    }

    public void setVibration(boolean vibration) {
        this.vibration = vibration;
    }
}
