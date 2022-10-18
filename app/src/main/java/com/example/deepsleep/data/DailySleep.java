package com.example.deepsleep.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class DailySleep {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private Date date; // taj dan pre ponoci => Noc 7-8 je 7.
    private long duration; // seconds

    public DailySleep(long id, Date date, long duration) {
        this.id = id;
        this.date = date;
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
