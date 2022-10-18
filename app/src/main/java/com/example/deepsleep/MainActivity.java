package com.example.deepsleep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationManagerCompat;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private final String CHANNEL_ID = "ALARM_CHANNEL";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

    }

    private void createNotificationChannel() {
        NotificationChannelCompat notificationChannel = new NotificationChannelCompat
                .Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_MAX)
                .setName("Alarm Channel")
                .setDescription("Channel for alarm notifications")
                .build();

        NotificationManagerCompat.from(this).createNotificationChannel(notificationChannel);
    }

}