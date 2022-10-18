package com.example.deepsleep.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.ZonedDateTime;

@Entity
public class Sleep {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long duration; // in seconds
    private long start;
    private long end;

    public Sleep(long id, long duration, long start, long end) {
        this.id = id;
        this.duration = duration;
        this.start = start;
        this.end = end;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}