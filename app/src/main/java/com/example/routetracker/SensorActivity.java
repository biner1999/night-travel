package com.example.routetracker;

import android.content.Loader;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.lang.reflect.Array;

public class SensorActivity extends MyBaseActivity {

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



    ConstraintLayout backGround;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);


//        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        sensorManagerAccel.registerListener((SensorEventListener) this, sensorManagerAccel.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ),SensorManager.SENSOR_DELAY_NORMAL );
//        sensorManagerGyro = (SensorManager) getSystemService(SENSOR_SERVICE);


        sensorManagerAccel = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelSensor = sensorManagerAccel.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        backGround = findViewById(R.id.constraintlayoutBackGround);



        TextView text_X = findViewById(R.id.textViewXValue);
        TextView text_Y = findViewById(R.id.textViewYValue);
        TextView text_Z = findViewById(R.id.textViewZValue);

        if (accelSensor == null){
            Toast.makeText(this, "The device has no Accel", Toast.LENGTH_SHORT).show();
            finish();
        }

         accelerometerEventListener = new SensorEventListener() {
             @Override
             public void onSensorChanged(SensorEvent sensorEvent) {
                 Sensor mySensor = sensorEvent.sensor;
                 //System.out.println("Changed");
                 //Toast.makeText(SensorActivity.this, "Changed", Toast.LENGTH_SHORT).show();

                 if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                     index = index + 1;
                     accelValuesX = sensorEvent.values[0];
                     accelValuesY = sensorEvent.values[1];
                     accelValuesZ = sensorEvent.values[2];
                     double rootSquare = Math.sqrt(Math.pow(accelValuesX, 2) + Math.pow(accelValuesY, 2) + Math.pow(accelValuesZ, 2));
                     System.out.println("Rootsquare = " + rootSquare);
                     if(rootSquare<2.0)
                     {
                         System.out.println("Fall Rootsquare = " + rootSquare);
                         backGround.setBackgroundColor(Color.RED);
                         Toast.makeText(SensorActivity.this, "Fall detected", Toast.LENGTH_SHORT).show();
                         text_X.setText(String.valueOf(rootSquare));
                     }
                 }
             }


             @Override
             public void onAccuracyChanged(Sensor sensor, int accuracy) {

             }


         };


//         Goes in Oncreate
    sensorManagerGyro = (SensorManager) getSystemService(SENSOR_SERVICE);
    gyroscopeSensor = sensorManagerGyro.getDefaultSensor(Sensor.TYPE_GYROSCOPE);


        if (gyroscopeSensor == null){
        Toast.makeText(this, "The device has no Gyroscope", Toast.LENGTH_SHORT).show();
        finish();
    }

    gyroscopeEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.values[2] > 0.5f){
                backGround.setBackgroundColor(Color.BLUE);
                resetDisconnectTimer();

            } else if (sensorEvent.values[2] < -0.5f){
                backGround.setBackgroundColor(Color.YELLOW);
                //Toast.makeText(SensorActivity.this, "YELLOW", Toast.LENGTH_SHORT).show();
                resetDisconnectTimer();

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

        configureBackButtonSettings();
        stopDisconnectTimer();

    }


    @Override
    public void onResume() {

        super.onResume();
        sensorManagerGyro.registerListener(gyroscopeEventListener,gyroscopeSensor,SensorManager.SENSOR_DELAY_FASTEST);
        sensorManagerAccel.registerListener(accelerometerEventListener,accelSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManagerAccel.unregisterListener(accelerometerEventListener);
        sensorManagerGyro.unregisterListener(gyroscopeEventListener);
    }


    private void configureBackButtonSettings(){
        Button backButton = findViewById(R.id.buttonSensorBack);
        backButton.setOnClickListener(v -> finish());
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
