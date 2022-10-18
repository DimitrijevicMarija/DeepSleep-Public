package com.example.deepsleep.data;

import androidx.lifecycle.LiveData;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DailySleepRepository {

    private final DailySleepDao dailySleepDao;
    private final ExecutorService executorService;

    public DailySleepRepository(DailySleepDao dailySleepDao, ExecutorService executorService) {
        this.dailySleepDao = dailySleepDao;
        this.executorService = executorService;
    }

    public void insert(DailySleep dailySleep){
        executorService.submit(()-> dailySleepDao.insert(dailySleep));
    }

    public void updateDuration(long sec, long id){
        executorService.submit(()-> dailySleepDao.updateDuration(sec, id));
    }


    public LiveData<List<DailySleep>> getDailySleepInclusively(Date start, Date end){
        return dailySleepDao.getDailySleepInclusively(start, end);
    }

    public DailySleep getDailySleep(Date d){
        Future< DailySleep> future = executorService.submit(()-> dailySleepDao.getDailySleep(d));
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
