package com.example.routetracker;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_ID_1 = "Foreground channel";
    public static final String CHANNEL_ID_2 = "Alerts channel";
    private static final String GROUP_ID_1 = "Group 1";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel1();

    }

    private void createNotificationChannel1() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannelGroup group1 = new NotificationChannelGroup(GROUP_ID_1, "Group 1");


            CharSequence name = getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_ID_1, "Foreground channel", importance);
            channel1.setGroup(GROUP_ID_1);
            NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID_2, "Alerts channel", importance);
            channel2.setGroup(GROUP_ID_1);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannelGroup(group1);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
        }
    }
}
