package com.example.routetracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.service.notification.StatusBarNotification;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class SensorService extends Service {

    public static final String CHANNEL_ID_1 = "Foreground channel";

    float accelValuesX;
    float accelValuesY;
    float accelValuesZ;
    static boolean trigger = false;

    String dest;
    String curr;
    long time;
    long timeSS;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        trigger=false;
        SensorManager sensorManagerAccel = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelSensor = Objects.requireNonNull(sensorManagerAccel).getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorManager sensorManagerGyro = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor gyroscopeSensor = Objects.requireNonNull(sensorManagerGyro).getDefaultSensor(Sensor.TYPE_GYROSCOPE);



        time = intent.getLongExtra("timeID", 0);
        timeSS = intent.getLongExtra("timeSS", 0);
        dest = intent.getStringExtra("dest");
        curr = intent.getStringExtra("curr");

        //Checks if accelerometer is present in device
        if (accelSensor == null){
            Toast.makeText(this, "The device has no Accelerometer", Toast.LENGTH_SHORT).show();
        }
        if (gyroscopeSensor == null){
            Toast.makeText(this, "The device has no Gyroscope", Toast.LENGTH_SHORT).show();
        }


        //Gyroscopic sensor for detecting if phone hasnt moved for 5mins
        SensorEventListener gyroscopeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                if(trigger){
                    sensorManagerGyro.unregisterListener(this);
                }

                if (sensorEvent.values[2] > 0.5f) {
                    //Resets timer
                    resetDisconnectTimer();
                } else if (sensorEvent.values[2] < -0.5f) {
                    //Resets timer
                    resetDisconnectTimer();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        //Accelerometer sensor for detecting if user has fallen
        SensorEventListener accelerometerEventListener = new SensorEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                Sensor mySensor = sensorEvent.sensor;
                if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    accelValuesX = sensorEvent.values[0];
                    accelValuesY = sensorEvent.values[1];
                    accelValuesZ = sensorEvent.values[2];
                    double rootSquare = Math.sqrt(Math.pow(accelValuesX, 2) + Math.pow(accelValuesY, 2) + Math.pow(accelValuesZ, 2));
                    if (rootSquare < 2.0) {
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        StatusBarNotification[] notifications = new StatusBarNotification[0];
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            assert mNotificationManager != null;
                            notifications = mNotificationManager.getActiveNotifications();
                        }
                        for (StatusBarNotification notification : notifications) {
                            //do nothing if notification is on the screen
                            if (notification.getId() != 2) {
                                sensorManagerAccel.unregisterListener(this);
                                startSensorTriggerService();
                            }
                        }
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };


        //Creates a notification
        Intent notificationIntent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_1)
                .setSmallIcon(R.drawable.ic_my_location)
                .setContentTitle("You're on route")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build();
        //starts in foreground to prevent shutting it down
        startForeground(1, notification);

        //Registers the sensors
        sensorManagerGyro.registerListener(gyroscopeEventListener, gyroscopeSensor,SensorManager.SENSOR_DELAY_FASTEST);
        sensorManagerAccel.registerListener(accelerometerEventListener, accelSensor,SensorManager.SENSOR_DELAY_NORMAL);

        startForeground(1, notification);

        return START_REDELIVER_INTENT;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        trigger = true;
    }

    public static final long DISCONNECT_TIMEOUT = 300000;

    private static Handler disconnectHandler = new Handler(msg -> true);

    //Resets the disconnect timer
    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    //When timer has elapsed this function is called
    private Runnable disconnectCallback = () -> {
        // Perform any required operation on disconnect
        DatabaseFunctions myDb = new DatabaseFunctions(this);
        Cursor res = myDb.getUserIDOne();
        res.moveToNext();

        double multiplier = res.getInt(13);
        double hundred = 100;
        double actualMultiplier = multiplier/hundred;


        long journeyTime = Math.round(time + time * 0.25 * actualMultiplier) + 300000;
        long time = journeyTime - timeSS;


         if ((TimeTriggerService.isRunning() && time>0) || (TimeLeftTriggerService.isRunning() && time>0)) {
            stopTimeTriggersService();
            stopTimeLeftTriggerService();
            startSensorTriggerService();
        }
        else {
            startSensorTriggerService();
        }
    };

    public void startSensorTriggerService() {
        trigger = true;
        Intent serviceIntent = new Intent(this, SensorTriggerService.class);
        serviceIntent.putExtra("dest", dest);
        serviceIntent.putExtra("curr", curr);
        serviceIntent.putExtra("timeID", time);
        startService(serviceIntent);
    }
    public void stopTimeTriggersService() {
        Intent serviceIntent = new Intent(this, TimeTriggerService.class);
        stopService(serviceIntent);
    }

    public void stopTimeLeftTriggerService() {
        Intent serviceIntent = new Intent(this, TimeLeftTriggerService.class);
        stopService(serviceIntent);
    }
}
