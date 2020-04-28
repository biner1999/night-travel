package com.example.routetracker;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

@SuppressLint("Registered")
public class NotificationsService extends Service {

    public static final String CHANNEL_ID_1 = "Foreground channel";

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {

        Intent notificationIntent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_1)
                .setSmallIcon(R.drawable.ic_my_location)
                .setContentTitle("You're on route")
                .setStyle(new NotificationCompat.InboxStyle()
                        .setSummaryText("ETA: X - Distance: X")
                        .addLine("ETA: X")
                        .addLine("Distance: X"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build();
        //starts in foreground to prevent shutting it down
        startForeground(1, notification);
        //restarts the service in case of crash with previous intent
        return START_REDELIVER_INTENT;


    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

