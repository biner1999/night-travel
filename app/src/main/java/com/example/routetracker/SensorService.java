package com.example.routetracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.example.routetracker.SensorActivity.CHANNEL_ID;


//stopSelf(); will stop the service, some method to stop the service is required either from within or outside
public class SensorService extends Service {

    private SensorManager sensorManagerGyro;
    private SensorManager sensorManagerAccel;

    private Sensor gyroscopeSensor;
    private Sensor accelSensor;

    private SensorEventListener gyroscopeEventListener;
    private SensorEventListener accelerometerEventListener;


    int index = 0;


    float accelValuesX;
    float accelValuesY;
    float accelValuesZ;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        String input = intent.getStringExtra("inputExtra");

        sensorManagerAccel = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelSensor = sensorManagerAccel.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelSensor == null){
            Toast.makeText(this, "The device has no Accelerometer", Toast.LENGTH_SHORT).show();
        }

        accelerometerEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                Sensor mySensor = sensorEvent.sensor;
                if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    index = index + 1;
                    accelValuesX = sensorEvent.values[0];
                    accelValuesY = sensorEvent.values[1];
                    accelValuesZ = sensorEvent.values[2];
                    double rootSquare = Math.sqrt(Math.pow(accelValuesX, 2) + Math.pow(accelValuesY, 2) + Math.pow(accelValuesZ, 2));
                    if(rootSquare<2.0)
                    {
                        //TODO add funcionality with bart's notification system
                        System.out.println("Fall Rootsquare = " + rootSquare);
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

        sensorManagerGyro = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManagerGyro.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (gyroscopeSensor == null){
            Toast.makeText(this, "The device has no Gyroscope", Toast.LENGTH_SHORT).show();
        }

        gyroscopeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.values[2] > 0.5f){
                    resetDisconnectTimer();
                    System.out.println("GYRO CHANGED");
                } else if (sensorEvent.values[2] < -0.5f){
                    resetDisconnectTimer();
                    System.out.println("GYRO CHANGED");
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        Intent notificationIntent = new Intent(this, SensorActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,notificationIntent,0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Sensor Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_android_sensor)
                .setContentIntent(pendingIntent)
                .build();
        sensorManagerGyro.registerListener(gyroscopeEventListener,gyroscopeSensor,SensorManager.SENSOR_DELAY_FASTEST);
        sensorManagerAccel.registerListener(accelerometerEventListener,accelSensor,SensorManager.SENSOR_DELAY_NORMAL);

        startForeground(1, notification);

        return START_NOT_STICKY;
    }


    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //TODO unregister listeners when the stop command is given
//        sensorManagerAccel.unregisterListener(accelerometerEventListener);
//        sensorManagerGyro.unregisterListener(gyroscopeEventListener);
    }

    //ToDo change to 5mins
    public static final long DISCONNECT_TIMEOUT = 5000;//300000; // 5 min = 5 * 60 * 1000 ms

    private static Handler disconnectHandler = new Handler(msg -> {
        // todo add functionality to bart's notification's
        return true;
    });

    public void resetDisconnectTimer(){
        System.out.println("Reset Disconnect Timer");
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

        public void stopDisconnectTimer(){
        System.out.println("Stop Disconnect Timer");
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

        public void startDisconnectTimer(){
        System.out.println("Start Disconnect Timer");
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    private static Runnable disconnectCallback = () -> {
        // Perform any required operation on disconnect
        System.out.println("Disconnect test");
    };




}
