package com.example.routetracker;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

//TODO 1. change phone numbers 2. combine sensor and foreground notificaiton 3. get values for foreground notification 4. run the correct timers and stuff

public class TimeLeftTriggerService extends Service {
    private static boolean isRunning;


    // First Time Trigger //
    public void FirstTriggerStart(long time) {
        long firstNotificationDelay = 5000; //TODO HERE
        long secondNotificationDelay = 5000; //60000; //3 mins
        long thirdNotificationDelay = 5000; //Math.round(time*0.15);
        long fourthNotificationDelay = 5000; //Math.round(time*0.60);
        final Handler handler = new Handler();
/* proper code, commented out for testing        handler.postDelayed(() -> {
            startL1Service();
            handler.postDelayed(() -> {
                startL2Service();
                handler.postDelayed(() -> {
                    startL3Service();
                    handler.postDelayed(() -> {
                        startL4Service();
                        stopSelf();
                    }, fourthNotificationDelay);
                }, thirdNotificationDelay);
            }, secondNotificationDelay);
        }, firstNotificationDelay);*/
        handler.postDelayed(() -> {
            startL1Service();
            handler.postDelayed(() -> {
                startL2Service();
                stopSelf();
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
        int timeID = intent.getIntExtra("timeID", 0);
        FirstTriggerStart(timeID);
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
        super.onDestroy();
    }

}
