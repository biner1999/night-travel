package com.example.routetracker;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

//import static com.example.routetracker.App.CHANNEL_ID;

public class L3NotificationsService extends Service {

    public static final String CHANNEL_ID_1 = "Foreground channel";
    public static final String CHANNEL_ID_2 = "Alerts channel";
    public static final String GROUP_ID_1 = "Group 1";
    private String phoneNumber = "07706473014";
    DatabaseFunctions myDb;

    public void sendSMS() {
        myDb = new DatabaseFunctions(this);
        Cursor res = myDb.getAllUserData();
        String x = "aaa";
        String y = "aaa";
        String z = "aaa";
        String textMessage = "This is an automated text sent by RouteTracker from " + x + ". He might be in danger on his journey from " + x + " to " + y + ". He phone is currently at " + z + ". You should contact him ASAP. A text to the police will be sent if he doesn't respond in " + x + " time.";

        boolean mSMSPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            mSMSPermissionGranted = true;
        } else {

        }

        if (mSMSPermissionGranted) {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> newTextMessage = smsManager.divideMessage(textMessage);
            smsManager.sendMultipartTextMessage(phoneNumber, null, newTextMessage, null, null);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        sendSMS();
        Intent activityIntent = new Intent(this, LoginActivity.class);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(R.drawable.ic_warning)
                .setContentTitle("Level 3 Alert")
                .setContentText("A text has been sent to " + "-name-." + " Enter the Route Tracker to give yourself more time")
                .setColor(Color.RED)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                //.setFullScreenIntent(contentIntent, true)
                .setContentIntent(activityPendingIntent)
                .setAutoCancel(true)
                .build();
        notification.flags |= Notification.FLAG_INSISTENT;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(2, notification);

/*
        Window window = .getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

*/


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

