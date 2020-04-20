package com.example.routetracker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.PasswordAuthentication;

public class LoginActivity extends AppCompatActivity {
    private EditText e2;
    private Button login, DEBUG;
    private DatabaseFunctions db;
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;

    private static final String TAG = "UserListFragment";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getPermission();
        isMapsEnabled();

        db = new DatabaseFunctions(this);

        if(checkUserExists()) {
            Intent newUserScreen = new Intent(LoginActivity.this, CreateUserActivity.class);
            startActivity(newUserScreen);
            LoginActivity.this.finish();
        }

        e2 = findViewById(R.id.login_pin);
        login = findViewById(R.id.login_btn);
        DEBUG = findViewById(R.id.DEBUG);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = e2.getText().toString();
                Boolean Chkpass = db.checkpassword(password);
                if(Chkpass) {
                    long timeSinceStart = System.currentTimeMillis() - homescreenActivity.startTimeRoute;
                    long journeyTime = Math.round((homescreenActivity.numTimeRoute * 1000)*1.25) + 300000;
                    long time = journeyTime - timeSinceStart;
                    Toast.makeText(getApplicationContext(), time + " " + journeyTime + " " + timeSinceStart, Toast.LENGTH_SHORT).show();
                    // login if user has an active route and NotificationRestarService is running instead of TimeTriggerService
                    if (NotificationRestartService.isRunning()) {
                        stopNotificationsRestartService(v);
                        startNotificationsRestartService(v);
                        Toast.makeText(getApplicationContext(), "Successfully logged in" + "NRS", Toast.LENGTH_SHORT).show();
                        Intent homeScreen = new Intent(LoginActivity.this, homescreenActivity.class);
                        startActivity(homeScreen);
                        finish();
                    }
                    // login if user has an active route and less than 3 minutes on the first timer
                    else if (TimeTriggerService.isRunning() && time<180000) {
                        stopTimeTriggersService(v);
                        stopTimeLeftTriggerService(v);
                        startNotificationsRestartService(v);
                        Toast.makeText(getApplicationContext(), "Successfully logged in" + "TTS", Toast.LENGTH_SHORT).show();
                        Intent homeScreen = new Intent(LoginActivity.this, homescreenActivity.class);
                        startActivity(homeScreen);
                        finish();
                    }
                    //login if sensor triggered the notifications
                    else if (SensorTriggerService.isRunning()) {
                        stopSensorTriggerService(v);
                        startTimeLeftTriggerService(v);
                        Toast.makeText(getApplicationContext(), "Successfully logged in" + "STS", Toast.LENGTH_SHORT).show();
                        Intent homeScreen = new Intent(LoginActivity.this, homescreenActivity.class);
                        startActivity(homeScreen);
                        finish();
                    }
                    //login if sensor got triggered earlier and the timer needs to run from a specific point along the journey
                    else if (TimeLeftTriggerService.isRunning()) {
                        Toast.makeText(getApplicationContext(), "Successfully logged in" + "TLTS", Toast.LENGTH_SHORT).show();
                        Intent homeScreen = new Intent(LoginActivity.this, homescreenActivity.class);
                        startActivity(homeScreen);
                        finish();
                    }
                    //login if user has no active route or has an active route and more than 3 minutes left on the first timer
                    else {
                        Toast.makeText(getApplicationContext(), "Successfully logged in" + "none", Toast.LENGTH_SHORT).show();
                        Intent homeScreen = new Intent(LoginActivity.this, homescreenActivity.class);
                        startActivity(homeScreen);
                        finish();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(),"Failed to log in",Toast.LENGTH_SHORT).show();
                }
            }
        });


        DEBUG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent DebugScreen = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(DebugScreen);
            }
        });
    }

    private boolean checkUserExists(){
        Cursor res = db.getAllUserData();
        return (res.getCount() == 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getPermission() {
        requestPermissions(new String[]{
                        //just add a permission in here for user to allow it
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
    }

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

    public void startNotificationsRestartService(View v) {
        Intent serviceIntent = new Intent(LoginActivity.this, NotificationRestartService.class);
        startService(serviceIntent);
    }

    public void stopNotificationsRestartService(View v) {
        Intent serviceIntent = new Intent(LoginActivity.this, NotificationRestartService.class);
        stopService(serviceIntent);
    }

    public void stopTimeTriggersService(View v) {
        Intent serviceIntent = new Intent(this, TimeTriggerService.class);
        stopService(serviceIntent);
    }

    public void stopSensorTriggerService(View v) {
        Intent serviceIntent = new Intent(this, SensorTriggerService.class);
        stopService(serviceIntent);
    }

    public void startTimeLeftTriggerService(View v) {
        long timeSinceStart = System.currentTimeMillis() - homescreenActivity.startTimeRoute;
        long journeyTime = Math.round((homescreenActivity.numTimeRoute * 1000)*1.25) + 300000;
        long time = journeyTime - timeSinceStart;
        Intent serviceIntent = new Intent(this, TimeLeftTriggerService.class);
        serviceIntent.putExtra("timeID", time);
        startService(serviceIntent);
    }

    public void stopTimeLeftTriggerService(View v) {
        Intent serviceIntent = new Intent(this, TimeLeftTriggerService.class);
        stopService(serviceIntent);
    }


}


