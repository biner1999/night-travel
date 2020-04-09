package com.example.routetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;
    public boolean b = false;

    /*    private void getPermission() {
            int PERMISSION_ALL = 1;
            String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS};
            if(!hasPermissions(this, PERMISSIONS)){
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
        }*/
    private void getPermission() {
        requestPermissions(new String[]{
                //just add a permission in here for user to allow it
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
    }
    //Test To See If Commit Changes
    //Additional Test To See If Changes Occur

    //EVEN MORE
    private void alertMessageNoGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS is disabled; GPS is required for this app to work").setCancelable(false)
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent enableGPSIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGPSIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    private void isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertMessageNoGPS();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();
        isMapsEnabled();

        createNewUser();
        sensorGoTo();

        Button testbtn = findViewById(R.id.testLoginBtn);
        testbtn.setOnClickListener(v -> {
            Intent nextScreen = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(nextScreen);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        b = false;

    }

    @Override
    public void onPause() {
        super.onPause();
        b = true;
    }

    private void createNewUser(){
        Button btnCreateUser = findViewById(R.id.button_Create_User);
        btnCreateUser.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CreateUserActivity.class)));
    }

    private void sensorGoTo(){
        Button btnCreateUser = findViewById(R.id.buttonSensor);
        btnCreateUser.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SensorActivity.class)));
    }



    public void startForegroundService(View v) {
        Intent serviceIntent = new Intent(this, NotificationsService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        }
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopNotificationService(View v) {
        Intent serviceIntent = new Intent(this, NotificationsService.class);
        stopService(serviceIntent);
    }

    public void startL1Service(View v) {
        Intent L1ServiceIntent = new Intent(this, L1NotificationsService.class);
        startService(L1ServiceIntent);
    }

    public void stopL1Service(View v) {
        Intent L1ServiceIntent = new Intent(this, L1NotificationsService.class);
        stopService(L1ServiceIntent);
    }

    public void startL2Service(View v) {
        Intent L2ServiceIntent = new Intent(this, L2NotificationsService.class);
        startService(L2ServiceIntent);
    }

    public void stopL2Service(View v) {
        Intent L2ServiceIntent = new Intent(this, L2NotificationsService.class);
        stopService(L2ServiceIntent);
    }

    public void startL3Service(View v) {
        Intent L3ServiceIntent = new Intent(this, L4NotificationsService.class);
        startService(L3ServiceIntent);
    }

    public void stopL3Service(View v) {
        Intent L3ServiceIntent = new Intent(this, L3NotificationsService.class);
        stopService(L3ServiceIntent);
    }



    public void startNotifications(View v) {
        final Handler handler = new Handler();
        boolean a = true;
        if (a) {
            startL1Service(v);
                handler.postDelayed(() -> {
                    if (b) {
                        startL2Service(v);
                    }
                }, 5000);

            //handler.postDelayed(() -> startL3Service(v), 15000);
        }
    }


}
