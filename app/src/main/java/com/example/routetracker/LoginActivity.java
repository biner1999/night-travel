package com.example.routetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
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
                if(Chkpass){
                    Toast.makeText(getApplicationContext(),"Successfully logged in",Toast.LENGTH_SHORT).show();
                    Intent homeScreen = new Intent(LoginActivity.this, homescreenActivity.class);
                    startActivity(homeScreen);}
                else
                    Toast.makeText(getApplicationContext(),"Failed to log in",Toast.LENGTH_SHORT).show();


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

}


