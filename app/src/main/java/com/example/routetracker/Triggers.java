package com.example.routetracker;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;



public class Triggers extends Service {

    // First Time Trigger //
    public void FirstTriggerStart(long time) {
        long delay = Math.round(time*1.25) + 5000;
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
                startL1Service();
        }, 5000);



    }
    public void startL1Service() {
        Intent L1ServiceIntent = new Intent(this, L1NotificationsService.class);
        startService(L1ServiceIntent);
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
