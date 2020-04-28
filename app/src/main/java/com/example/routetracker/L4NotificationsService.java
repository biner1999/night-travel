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


public class L4NotificationsService extends Service {

    public static final String CHANNEL_ID_2 = "Alerts channel";
    DatabaseFunctions myDb;
    String dest;
    String curr;

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

        String textMessage = "This is an automated text sent by RouteTracker from " + FirstName + " " + LastName + ". They might be in danger on their journey to " + dest + ". Their phone is currently at " + curr + ". Their age is " + Age + ". Their height is " + Height + ". Their weight is " + Weight + ". Their hair colour is " + HairColour + ". Their ethnicity is " + Ethnicity;

        boolean mSMSPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            mSMSPermissionGranted = true;
        }

        if (mSMSPermissionGranted) {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> newTextMessage = smsManager.divideMessage(textMessage);
            String phoneNumber = "07706473014";
            smsManager.sendMultipartTextMessage(phoneNumber, null, newTextMessage, null, null);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        dest = intent.getStringExtra("dest");
        curr = intent.getStringExtra("curr");
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
