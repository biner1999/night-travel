package com.example.routetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class SensorActivity extends MyBaseActivity {

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SensorEventListener gyroscopeEventListener;

    ConstraintLayout backGround;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        backGround = findViewById(R.id.constraintlayoutBackGround);

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
                    Toast.makeText(SensorActivity.this, "YELLOW", Toast.LENGTH_SHORT).show();
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
        sensorManager.registerListener(gyroscopeEventListener,gyroscopeSensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(gyroscopeEventListener);
    }


    private void configureBackButtonSettings(){
        Button backButton = findViewById(R.id.buttonSensorBack);
        backButton.setOnClickListener(v -> finish());
    }
}
