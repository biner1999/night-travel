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

//import static com.example.routetracker.App.CHANNEL_ID;

public class L4NotificationsService extends Service {

    public static final String CHANNEL_ID_1 = "Foreground channel";
    public static final String CHANNEL_ID_2 = "Alerts channel";
    public static final String GROUP_ID_1 = "Group 1";
    private String phoneNumber = "07706473014";
    DatabaseFunctions myDb;

    public void sendSMS() {
        myDb = new DatabaseFunctions(this);
        Cursor res = myDb.getAllUserData();
        res.moveToNext();
        String FirstName = res.getString(1);
        String LastName = res.getString(2);
        int Age = res.getInt(4);
        int Height = res.getInt(5);
        int Weight = res.getInt(7);
        String HairColour = res.getString(6);
        String Ethnicity = res.getString(8);

        String x = "3123123";

        String textMessage = "This is an automated text sent by RouteTracker from " + FirstName + " " + LastName + ". He might be in danger on his journey from " + x + " to " + x + ". He phone is currently at " + x + ". His age is " + Age + ". His height is " + Height + ". His weight is " + Weight + ". His hair colour is " + HairColour + ". His ethnicity is " + Ethnicity;

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
                .setContentTitle("Level 4 Alert")
                .setContentText("A text has been sent to the police. Enter the Route Tracker to give yourself more time") ///////////////CHANGE THIS
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
