package com.example.deepsleep.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SleepDao {

    @Insert
    long insert(Sleep sleep);

    @Query("SELECT * FROM Sleep")
    List<Sleep> getAll();

    @Query("SELECT * FROM Sleep")
    LiveData<List<Sleep>> getAllLiveData();

    @Query("SELECT * FROM Sleep WHERE id = (select max(id) from Sleep)")
    LiveData<Sleep> getLastSleep();

    @Query("SELECT * FROM Sleep WHERE `end` >= :boundary1 and `end` < :boundary2  ORDER BY start")
    LiveData<List<Sleep>> getAllSleepForDay(long boundary1, long boundary2);
}