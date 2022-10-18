package com.example.deepsleep.statistics;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.deepsleep.data.DailySleep;
import com.example.deepsleep.data.DailySleepRepository;
import com.example.deepsleep.data.Sleep;
import com.example.deepsleep.data.SleepRepository;

import java.util.Date;
import java.util.List;

public class SleepViewModel extends ViewModel {

    private final SleepRepository sleepRepository;
    private final DailySleepRepository dailySleepRepository;
    private int column = 7;

    public SleepViewModel(SleepRepository sleepRepository, DailySleepRepository dailySleepRepository) {
        this.sleepRepository = sleepRepository;
        this.dailySleepRepository = dailySleepRepository;
    }

    public LiveData<Sleep> getLastSleep(){
        return sleepRepository.getLastSleep();
    }

    public LiveData<List<Sleep>> getAllSleepLiveData(){
        return sleepRepository.getAllLiveData();
    }

    public LiveData<List<Sleep>> getAllSleepForDay(long boundary1, long boundary2){
        return sleepRepository.getAllSleepForDay(boundary1, boundary2);
    }
    public LiveData<List<DailySleep>> getDailySleepInclusively(Date start, Date end){
        return dailySleepRepository.getDailySleepInclusively(start, end);
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
