package com.example.deepsleep.data;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class AlarmRepository {

    private final AlarmDao alarmDao;
    private final ExecutorService executorService;

    public AlarmRepository(AlarmDao alarmDao, ExecutorService executorService) {
        this.alarmDao = alarmDao;
        this.executorService = executorService;
    }

    public long insert(Alarm alarm){
        Future<Long> future = executorService.submit(()-> alarmDao.insert(alarm));
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return -1;

    }

    public List<Alarm> getAlarmsForReboot(long now){
        List<Alarm> result = new ArrayList<>();
        Future< List<Alarm>> future = executorService.submit(()-> alarmDao.getAlarmsForReboot(now));
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }


    public void update(Alarm alarm){
        executorService.submit(()->{
            alarmDao.update(alarm);
        });

    }

    public void updateValidityOfAlarm(boolean valid, long id){
        executorService.submit(()->{
            alarmDao.updateValidityOfAlarm(valid, id);
        });
    }

    public void delete(Alarm alarm){
        executorService.submit(()->{
            alarmDao.delete(alarm);
        });

    }

    public LiveData<List<Alarm>> getAllLiveData(){
        return alarmDao.getAllLiveData();
    }
}
