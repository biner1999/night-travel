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
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class L3NotificationsService extends Service {

    public static final String CHANNEL_ID_2 = "Alerts channel";
    DatabaseFunctions myDb;
    String dest;
    String curr;
    long time;


    public void sendSMS() {
        myDb = new DatabaseFunctions(this);
        Cursor res = myDb.getAllUserData();
        res.moveToNext();
        String FirstName = res.getString(1);
        String LastName = res.getString(2);
        String phoneNumber = res.getString(14);

        double timeAfter = time*0.60;
        long timeUntilPolice = TimeUnit.MILLISECONDS.toMinutes((long) timeAfter);

        String textMessage = "This is an automated text sent by RouteTracker from " + FirstName + " " + LastName + ". He might be in danger on his journey to " + dest + ". His phone is currently at " + curr + ". You should contact him ASAP. A text to the police will be sent if he doesn't respond in about " + timeUntilPolice + " minutes.";

        boolean mSMSPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            mSMSPermissionGranted = true;
        }

        if (mSMSPermissionGranted) {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> newTextMessage = smsManager.divideMessage(textMessage);
            smsManager.sendMultipartTextMessage(phoneNumber, null, newTextMessage, null, null);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        dest = intent.getStringExtra("dest");
        curr = intent.getStringExtra("curr");
        time = intent.getLongExtra("time", 0);
        sendSMS();
        Intent activityIntent = new Intent(this, LoginActivity.class);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(R.drawable.ic_warning)
                .setContentTitle("Level 3 Alert")
                .setContentText("A text has been sent to your emergency contact. Enter the Route Tracker to give yourself more time")
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

