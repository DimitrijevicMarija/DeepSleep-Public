package com.example.deepsleep.data;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class SleepRepository {

    private final SleepDao sleepDao;
    private final ExecutorService executorService;

    public SleepRepository(SleepDao sleepDao, ExecutorService executorService) {
        this.sleepDao = sleepDao;
        this.executorService = executorService;
    }

    public void insert(Sleep sleep){
        executorService.submit(()-> sleepDao.insert(sleep));
    }

    public List<Sleep> getAll(){
        List<Sleep> result = new ArrayList<>();
        Future< List<Sleep>> future = executorService.submit(()-> sleepDao.getAll());
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public LiveData<List<Sleep>> getAllLiveData(){
        return sleepDao.getAllLiveData();
    }

    public LiveData<Sleep> getLastSleep(){
        return sleepDao.getLastSleep();

    }
    public LiveData<List<Sleep>> getAllSleepForDay(long boundary1, long boundary2){
        return sleepDao.getAllSleepForDay(boundary1, boundary2);
    }
}
