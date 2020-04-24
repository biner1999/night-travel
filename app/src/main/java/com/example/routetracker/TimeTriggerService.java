package com.example.routetracker;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

//TODO 1. change phone numbers and set correct timers

public class TimeTriggerService extends Service {
    DatabaseFunctions myDb;
    private static boolean isRunning;
    final Handler handler = new Handler();
    String dest;
    String curr;
    long time;
    double actualMultiplier;
    int level4 = 0;


    // First Time Trigger //
    public void FirstTriggerStart() {
        long firstNotificationDelay = Math.round(time+time*0.25*actualMultiplier) + 300000; //25% + 5 mins
        long secondNotificationDelay = 60000;
        long thirdNotificationDelay = Math.round(time*0.15*actualMultiplier);
        long fourthNotificationDelay = Math.round(time*0.60*actualMultiplier);

        handler.postDelayed(() -> {
            startL1Service();
            handler.postDelayed(() -> {
                startL2Service();
                handler.postDelayed(() -> {
                    startL3Service();
                    if (level4 == 1) {
                        handler.postDelayed(() -> {
                            startL4Service();
                            stopSelf();
                        }, fourthNotificationDelay);
                    }
                    else {
                        stopSelf();
                    }
                }, thirdNotificationDelay);
            }, secondNotificationDelay);
        }, firstNotificationDelay);
    }
    public void startL1Service() {
        Intent L1ServiceIntent = new Intent(this, L1NotificationsService.class);
        startService(L1ServiceIntent);
    }
    public void startL2Service() {
        Intent L2ServiceIntent = new Intent(this, L2NotificationsService.class);
        startService(L2ServiceIntent);
    }
    public void startL3Service() {
        Intent L3ServiceIntent = new Intent(this, L3NotificationsService.class);
        L3ServiceIntent.putExtra("dest", dest);
        L3ServiceIntent.putExtra("curr", curr);
        L3ServiceIntent.putExtra("time", time);
        startService(L3ServiceIntent);
    }
    public void startL4Service() {
        Intent L4ServiceIntent = new Intent(this, L4NotificationsService.class);
        L4ServiceIntent.putExtra("dest", dest);
        L4ServiceIntent.putExtra("curr", curr);
        startService(L4ServiceIntent);
    }

    public static boolean isRunning() {
        return isRunning;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;

        myDb = new DatabaseFunctions(this);
        Cursor res = myDb.getAllUserData();
        res.moveToNext();

        double multiplier = res.getInt(13);
        double hundred = 100;
        actualMultiplier = multiplier/hundred;

        level4 = res.getInt(15);

        time = intent.getLongExtra("timeID", 0);
        dest = intent.getStringExtra("dest");
        curr = intent.getStringExtra("curr");
        FirstTriggerStart();
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
