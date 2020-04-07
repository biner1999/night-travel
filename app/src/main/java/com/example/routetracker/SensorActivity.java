package com.example.routetracker;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Array;
//TODO DELETE AT SOMEPOINT
public class SensorActivity extends Activity {
    ConstraintLayout backGround;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
//        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        sensorManagerAccel.registerListener((SensorEventListener) this, sensorManagerAccel.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ),SensorManager.SENSOR_DELAY_NORMAL );
//        sensorManagerGyro = (SensorManager) getSystemService(SENSOR_SERVICE);

        createNotificationChannel();


    }


    @Override
    public void onResume() {
        super.onResume();
       // sensorManagerGyro.registerListener(gyroscopeEventListener,gyroscopeSensor,SensorManager.SENSOR_DELAY_FASTEST);
       // sensorManagerAccel.registerListener(accelerometerEventListener,accelSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause(){
        super.onPause();
        //sensorManagerAccel.unregisterListener(accelerometerEventListener);
        //sensorManagerGyro.unregisterListener(gyroscopeEventListener);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }


    private void configureBackButtonSettings(){
        Button backButton = findViewById(R.id.buttonSensorBack);
        backButton.setOnClickListener(v -> finish());
    }

    public static final  String CHANNEL_ID = "exampleSensorChannel";

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Sensor Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);


            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    public void startService(View v){
        Intent serviceIntent = new Intent(this, SensorService.class);
        serviceIntent.putExtra("inputExtra", "Test");
        ContextCompat.startForegroundService(this,serviceIntent);
    }

    public void stopService(View v){
        Intent serviceIntent = new Intent(this, SensorService.class);
        stopService(serviceIntent);
    }






// Goes in Oncreate
//    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//    gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
//
//    backGround = findViewById(R.id.constraintlayoutBackGround);
//
//        if (gyroscopeSensor == null){
//        Toast.makeText(this, "The device has no Gyroscope", Toast.LENGTH_SHORT).show();
//        finish();
//    }
//
//    gyroscopeEventListener = new SensorEventListener() {
//        @Override
//        public void onSensorChanged(SensorEvent sensorEvent) {
//            if (sensorEvent.values[2] > 0.5f){
//                backGround.setBackgroundColor(Color.BLUE);
//                resetDisconnectTimer();
//
//            } else if (sensorEvent.values[2] < -0.5f){
//                backGround.setBackgroundColor(Color.YELLOW);
//                Toast.makeText(SensorActivity.this, "YELLOW", Toast.LENGTH_SHORT).show();
//                resetDisconnectTimer();
//
//            }
//        }
//
//        @Override
//        public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//        }
//    };
}
