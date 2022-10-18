package com.example.deepsleep.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlarmDao {

    @Insert
    long insert(Alarm alarm);

    @Update
    void update(Alarm alarm);

    @Delete
    void delete(Alarm alarm);

    @Query("SELECT * FROM Alarm ORDER BY time")
    LiveData<List<Alarm>> getAllLiveData();

    @Query("UPDATE Alarm SET valid = :valid WHERE id = :id")
    void updateValidityOfAlarm(boolean valid, long id);

    @Query("SELECT * FROM Alarm where valid = 1 AND millis > :now")
    List<Alarm> getAlarmsForReboot(long now);
}
