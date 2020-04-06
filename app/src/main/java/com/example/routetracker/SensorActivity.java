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

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SensorEventListener gyroscopeEventListener;
    private SensorEventListener accelerometerEventListener;

    TextView text_X;
    TextView text_Y;
    TextView text_Z;
    int index = 0;


    float[] accelValuesX;
    float[] accelValuesY;
    float[] accelValuesZ;



    ConstraintLayout backGround;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);


//        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        sensorManager.registerListener((SensorEventListener) this, sensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ),SensorManager.SENSOR_DELAY_NORMAL );


        text_X = findViewById(R.id.textViewXValue);
        text_Y = findViewById(R.id.textViewYValue);
        text_Z = findViewById(R.id.textViewZValue);

         accelerometerEventListener = new SensorEventListener() {

             @Override
             public void onSensorChanged(SensorEvent sensorEvent) {
                 // TODO Auto-generated method stub
                 Sensor mySensor = sensorEvent.sensor;

                 if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                     index++;
                     accelValuesX[index] = sensorEvent.values[0];
                     accelValuesY[index] = sensorEvent.values[1];
                     accelValuesZ[index] = sensorEvent.values[2];
                     if(index >= 127){
                         index = 0;
//                         accelManage.unregisterListener(this);
//                         callFallRecognition();
//                         accelManage.registerListener(this, senseAccel, SensorManager.SENSOR_DELAY_NORMAL);
                     }
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
//        sensorManager.registerListener(gyroscopeEventListener,gyroscopeSensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause(){
        super.onPause();
//        sensorManager.unregisterListener(gyroscopeEventListener);
    }


    private void configureBackButtonSettings(){
        Button backButton = findViewById(R.id.buttonSensorBack);
        backButton.setOnClickListener(v -> finish());
    }



    private void showValue(SensorEvent event){
        float[] Value = event.values;//array che contiene i valori dell'accelerometro
        //modifica del valore delle textView

        text_X.setText("Value X: "+Value[0]);
        text_Y.setText("Value Y: "+Value[1]);
        text_Z.setText("Value Z: "+Value[2]);

        System.out.println("Testing If I am here");

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
