package com.example.routetracker;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

//TODO 1. change phone numbers and set correct timers

public class TimeTriggerService extends Service {
    private static boolean isRunning;
    final Handler handler = new Handler();
    String dest;
    String curr;
    long time;

    // First Time Trigger //
    public void FirstTriggerStart() {
        long firstNotificationDelay = 5000; //Math.round(time*1.25) + 300000; //25% + 5 mins
        long secondNotificationDelay = 5000; //60000; //3 mins
        long thirdNotificationDelay = 5000; //Math.round(time*0.15);
        long fourthNotificationDelay = 5000; //Math.round(time*0.60);

/*        handler.postDelayed(() -> {
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
