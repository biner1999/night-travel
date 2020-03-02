package com.example.routetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;

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

        Button testbtn = findViewById(R.id.testLoginBtn);
        testbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextScreen = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(nextScreen);
            }
        });
    }

    private void createNewUser(){
        Button btnCreateUser = findViewById(R.id.button_Create_User);
        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateUserActivity.class));
            }
        });
    }



    public void startForegroundService(View v) {
        Intent serviceIntent = new Intent(this, NotificationsService.class);
        startForegroundService(serviceIntent);
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
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startService(L2ServiceIntent);
            }
        }, 10000);

    }

    public void stopL2Service(View v) {
        Intent L2ServiceIntent = new Intent(this, L2NotificationsService.class);
        stopService(L2ServiceIntent);
    }

    public void startL3Service(View v) {
        Intent L3ServiceIntent = new Intent(this, L3NotificationsService.class);
        startService(L3ServiceIntent);
    }

    public void stopL3Service(View v) {
        Intent L3ServiceIntent = new Intent(this, L3NotificationsService.class);
        stopService(L3ServiceIntent);
    }
    //to be moved out of here
/*    public void sendSMS(View v) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("07706473014", null, "Working", null, null);
    }*/


}
