package com.example.routetracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

//TODO likely needs changing?
import static com.example.routetracker.SensorActivity.CHANNEL_ID;


//stopSelf(); will stop the service, some method to stop the service is required either from within or outside
public class SensorService extends Service {

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

        //Declarations
        String input = intent.getStringExtra("inputExtra");
        SensorManager sensorManagerAccel = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelSensor = sensorManagerAccel.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorManager sensorManagerGyro = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor gyroscopeSensor = sensorManagerGyro.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        //Checks if accelerometer is present in device
        if (accelSensor == null){
            Toast.makeText(this, "The device has no Accelerometer", Toast.LENGTH_SHORT).show();
        }
        if (gyroscopeSensor == null){
            Toast.makeText(this, "The device has no Gyroscope", Toast.LENGTH_SHORT).show();
        }

        //Accelerometer sensor for detecting if user has fallen
        SensorEventListener accelerometerEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                Sensor mySensor = sensorEvent.sensor;
                if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    accelValuesX = sensorEvent.values[0];
                    accelValuesY = sensorEvent.values[1];
                    accelValuesZ = sensorEvent.values[2];
                    double rootSquare = Math.sqrt(Math.pow(accelValuesX, 2) + Math.pow(accelValuesY, 2) + Math.pow(accelValuesZ, 2));
                    if (rootSquare < 2.0) {
                        //TODO add funcionality with bart's notification system, this is the accelerometer
                        System.out.println("Fall Rootsquare = " + rootSquare);
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        //Gyroscopic sensor for detecting if phone hasnt moved for 5mins
        SensorEventListener gyroscopeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.values[2] > 0.5f) {
                    //Resets timer
                    resetDisconnectTimer();
                    System.out.println("GYRO CHANGED");
                } else if (sensorEvent.values[2] < -0.5f) {
                    //Resets timer
                    resetDisconnectTimer();
                    System.out.println("GYRO CHANGED");
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };


        //Creates a notification
        Intent notificationIntent = new Intent(this, SensorActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,notificationIntent,0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Sensor's Active")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_android_sensor)
                .setContentIntent(pendingIntent)
                .build();

        //Registers the sensors
        sensorManagerGyro.registerListener(gyroscopeEventListener, gyroscopeSensor,SensorManager.SENSOR_DELAY_FASTEST);
        sensorManagerAccel.registerListener(accelerometerEventListener, accelSensor,SensorManager.SENSOR_DELAY_NORMAL);


        //TODO implement a stop condition?
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
        return true;
    });

    //Resets the disconnect timer
    public void resetDisconnectTimer(){
        System.out.println("Reset Disconnect Timer");
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }
    //Stops the disconnect timer
        public void stopDisconnectTimer(){
        System.out.println("Stop Disconnect Timer");
        disconnectHandler.removeCallbacks(disconnectCallback);
    }
    //Starts the disconnect timer
        public void startDisconnectTimer(){
        System.out.println("Start Disconnect Timer");
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }
    // TODO add functionality to bart's notification's, this is the gyroscopes
    //When timer has elapsed this function is called
    private static Runnable disconnectCallback = () -> {
        // Perform any required operation on disconnect
        System.out.println("Disconnect test");
    };

}
