package com.example.deepsleep.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@TypeConverters(value = {DateConverter.class})
@Database(entities = {Alarm.class, Sleep.class, DailySleep.class}, version = 4, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {

    public abstract AlarmDao alarmDao();
    public abstract SleepDao sleepDao();
    public abstract DailySleepDao dailySleepDao();

    private static final String DATABASE_NAME = "deep-sleep-app.db";
    private static MyDatabase instance = null;
    private static ExecutorService executorService = null;

    public static MyDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (MyDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            MyDatabase.class,
                            DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }

    public static ExecutorService getInstanceExecutor(){
        if (executorService == null){
            synchronized (MyDatabase.class){
                if (executorService == null){
                    executorService = Executors.newFixedThreadPool(4);
                }
            }

        }
        return executorService;
    }
}
