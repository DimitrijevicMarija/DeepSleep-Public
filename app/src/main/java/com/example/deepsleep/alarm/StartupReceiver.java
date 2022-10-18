package com.example.deepsleep.alarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.deepsleep.R;
import com.example.deepsleep.data.Alarm;
import com.example.deepsleep.data.AlarmRepository;
import com.example.deepsleep.data.MyDatabase;

import java.util.List;

public class StartupReceiver extends BroadcastReceiver {

    private AlarmManager alarmManager;
    private Context context;


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("MIKA", "Reboot 1");
        //if (!intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) return;
        long now = System.currentTimeMillis();
        this.context = context;
        Log.d("MIKA", "Reboot 2");


        alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);


        MyDatabase myDatabase = MyDatabase.getInstance(this.context);
        AlarmRepository alarmRepository = new AlarmRepository(myDatabase.alarmDao(), MyDatabase.getInstanceExecutor());
        List<Alarm> alarms = alarmRepository.getAlarmsForReboot(now);
        for (Alarm alarm: alarms){
            setAlarm(alarm);
        }
        Log.d("MIKA", "Reboot 3");

    }

    private void setAlarm(Alarm alarm){


        Intent intent = new Intent(this.context, AlarmReceiver.class);
        intent.putExtra(context.getString(R.string.vibration_name_extra), alarm.isVibration());
        intent.putExtra(context.getString(R.string.morning_test_extra), alarm.isMorningTest());
        intent.putExtra(context.getString(R.string.volume_name_extra), alarm.getVolume());
        intent.putExtra(context.getString(R.string.id_alarm_extra), alarm.getId());

        int requestCode = (int) alarm.getId();
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this.context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(alarm.getMillis(), null);
        alarmManager.setAlarmClock(alarmClockInfo, alarmIntent);

    }
}
