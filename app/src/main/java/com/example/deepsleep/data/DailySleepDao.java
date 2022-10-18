package com.example.deepsleep.data;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface DailySleepDao {

    @Insert
    long insert(DailySleep dailySleep);

    @Query("UPDATE DailySleep SET duration = duration + :sec WHERE id = :id")
    void updateDuration(long sec, long id);


    @Query("SELECT * FROM DailySleep WHERE date >= :start and date <= :end ORDER BY date")
    LiveData<List<DailySleep>> getDailySleepInclusively(Date start, Date end);

    @Query("SELECT * FROM DailySleep where date = :d")
    DailySleep getDailySleep(Date d);
}
