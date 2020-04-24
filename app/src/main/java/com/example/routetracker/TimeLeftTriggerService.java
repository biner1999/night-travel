package com.example.routetracker;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class TimeLeftTriggerService extends Service {
    DatabaseFunctions myDb;
    private static boolean isRunning;
    final Handler handler = new Handler();
    String dest;
    String curr;
    long time;
    long timeSinceStart;
    long journeyTime;
    long timeLeft;
    double actualMultiplier;
    int level4 = 0;

    // First Time Trigger //
    public void FirstTriggerStart() {
        long firstNotificationDelay = timeLeft;
        long secondNotificationDelay = 60000; //1 mins
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
        startService(L3ServiceIntent);
    }
    public void startL4Service() {
        Intent L4ServiceIntent = new Intent(this, L4NotificationsService.class);
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
        timeSinceStart = intent.getLongExtra("timeSS", 0);
        dest = intent.getStringExtra("dest");
        curr = intent.getStringExtra("curr");

        journeyTime = Math.round(time+time*0.25*actualMultiplier) + 300000;
        timeLeft = journeyTime - timeSinceStart;

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
        handler.removeCallbacksAndMessages(null);
        isRunning = false;
        super.onDestroy();
    }

}
