package com.example.routetracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

//import static com.example.routetracker.App.CHANNEL_ID;

public class NotificationsService extends Service {

    public static final String CHANNEL_ID_1 = "Foreground channel";
    public static final String CHANNEL_ID_2 = "Alerts channel";
    public static final String GROUP_ID_1 = "Group 1";

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


/*    private void addNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Level 1 Alert")
                .setContentText("Level 1 Alert")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(1, builder.build());

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }*/

