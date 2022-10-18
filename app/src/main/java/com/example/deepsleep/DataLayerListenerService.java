package com.example.deepsleep;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.deepsleep.data.DailySleep;
import com.example.deepsleep.data.DailySleepRepository;
import com.example.deepsleep.data.MyDatabase;
import com.example.deepsleep.data.Sleep;
import com.example.deepsleep.data.SleepRepository;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

public class DataLayerListenerService extends WearableListenerService {

    private static final String SECONDS_KEY = "com.example.deepsleep.seconds";
    private static final String START_KEY = "com.example.deepsleep.start";
    private static final String END_KEY = "com.example.deepsleep.end";

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);

        Log.d("MIKAMSG", "--- NEW MESSAGE : ");
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {

                DataItem item = event.getDataItem();
                Uri uri = item.getUri();
                // String host = uri.getHost();
                if (uri.getPath().compareTo("/sleep") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    long seconds = dataMap.getLong(SECONDS_KEY);
                    long start = dataMap.getLong(START_KEY);
                    long end = dataMap.getLong(END_KEY);

                    ZonedDateTime startTime = Instant.ofEpochSecond(start).atZone( ZoneId.of("Europe/Belgrade"));
                    ZonedDateTime endTime = Instant.ofEpochSecond(end).atZone( ZoneId.of("Europe/Belgrade"));
                    Log.d("MIKAMSG", startTime + " - " + endTime);
                    Log.d("MIKAMSG", "seconds : " + seconds);

                    Sleep sleep = new Sleep(0, seconds, start, end);
                    MyDatabase myDatabase = MyDatabase.getInstance(this);
                    SleepRepository sleepRepository = new SleepRepository(myDatabase.sleepDao(), MyDatabase.getInstanceExecutor());
                    DailySleepRepository dailySleepRepository = new DailySleepRepository(myDatabase.dailySleepDao(), MyDatabase.getInstanceExecutor());



                    sleepRepository.insert(sleep);


                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(sleep.getEnd() * 1000);
                    if (calendar.get(Calendar.HOUR_OF_DAY) >= 21){
                        calendar.add(Calendar.DAY_OF_MONTH, 1);

                    }
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    Date date = calendar.getTime(); // date without time!!!



                    DailySleep dailySleep = dailySleepRepository.getDailySleep(date);
                    if (dailySleep == null){
                        dailySleep = new DailySleep(0, date, seconds);
                        dailySleepRepository.insert(dailySleep);
                    }
                    else {
                        dailySleepRepository.updateDuration(seconds, dailySleep.getId());
                    }


                }
            }
        }


    }
}
