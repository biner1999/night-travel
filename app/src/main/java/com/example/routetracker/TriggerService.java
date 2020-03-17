package com.example.routetracker;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;



public class TriggerService extends Service {

    // First Time Trigger //
    public void FirstTriggerStart(long time) {
        long firstNotificationDelay = 5000; //Math.round(time*1.25) + 300000; //25% + 5 mins
        long secondNotificationDelay = 5000; //60000; //3 mins
        long thirdNotificationDelay = 5000; //Math.round(time*0.15);
        long fourthNotificationDelay = 5000; //Math.round(time*0.60);
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
                startL1Service();
                handler.postDelayed(() -> {
                    startL2Service();
                    handler.postDelayed(() -> {
                        startL3Service();
                        handler.postDelayed(() -> {
                            startL4Service();
                        }, fourthNotificationDelay);
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int timeID = intent.getIntExtra("timeID", 0);
        FirstTriggerStart(timeID);
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
