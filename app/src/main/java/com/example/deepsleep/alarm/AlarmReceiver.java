package com.example.deepsleep.alarm;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.example.deepsleep.MainActivity;
import com.example.deepsleep.R;

public class AlarmReceiver extends BroadcastReceiver {

    private final String CHANNEL_ID = "ALARM_CHANNEL";
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "com.example.proba:tag");
        wl.acquire();


        boolean vibration = intent.getBooleanExtra(context.getString(R.string.vibration_name_extra), false);
        boolean morningTest = intent.getBooleanExtra(context.getString(R.string.morning_test_extra), false);
        float volume = intent.getFloatExtra(context.getString(R.string.volume_name_extra), 50);
        long id = intent.getLongExtra(context.getString(R.string.id_alarm_extra), -1);



        Intent fullScreenIntent = new Intent(context, AlarmActivity.class);
        fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        fullScreenIntent.putExtra(context.getString(R.string.vibration_name_extra), vibration);
        fullScreenIntent.putExtra(context.getString(R.string.morning_test_extra), morningTest);
        fullScreenIntent.putExtra(context.getString(R.string.volume_name_extra), volume);
        fullScreenIntent.putExtra(context.getString(R.string.id_alarm_extra), id);

        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        int color = context.getResources().getColor(R.color.secondaryColor);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.alarm_20px)
                        .setContentTitle(context.getString(R.string.alarm_notification_name))
                        .setContentText(context.getString(R.string.alarm_notification_text))
                        .setColor(color)
                        .setColorized(true)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        //.setAutoCancel(true)

                        // Use a full-screen intent only for the highest-priority alerts where you
                        // have an associated activity that you would like to launch after the user
                        // interacts with the notification. Also, if your app targets Android 10
                        // or higher, you need to request the USE_FULL_SCREEN_INTENT permission in
                        // order for the platform to invoke this notification.
                        .setFullScreenIntent(fullScreenPendingIntent, true);

        Notification incomingCallNotification = notificationBuilder.build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(123, incomingCallNotification);






           wl.release();


    }


}
