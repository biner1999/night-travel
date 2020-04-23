package com.example.routetracker;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;


public class SensorTriggerService extends Service {
    DatabaseFunctions myDb;
    private static boolean isRunning;
    final Handler handler = new Handler();
    String dest;
    String curr;
    long time;
    double actualMultiplier;

    // First Time Trigger //
    public void FirstTriggerStart() {
        long secondNotificationDelay = 60000; //1 mins
        long thirdNotificationDelay = Math.round(time*0.15*actualMultiplier);
        long fourthNotificationDelay = Math.round(time*0.60*actualMultiplier);
        stopTimeTriggersService();
        stopTimeLeftTriggerService();
        stopNotificationsRestartService();
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

    public void stopTimeLeftTriggerService() {
        Intent serviceIntent = new Intent(this, TimeLeftTriggerService.class);
        stopService(serviceIntent);
    }

    public void stopTimeTriggersService() {
        Intent serviceIntent = new Intent(this, TimeTriggerService.class);
        stopService(serviceIntent);
    }

    public void stopNotificationsRestartService() {
        Intent serviceIntent = new Intent(this, NotificationRestartService.class);
        stopService(serviceIntent);
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
        handler.removeCallbacksAndMessages(null);
        isRunning = false;
        super.onDestroy();
    }
}
