package com.example.routetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);


        //milliseconds
        int SPLASH_DISPLAY_TIMER = 2500;
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashscreenActivity.this, LoginActivity.class));
            SplashscreenActivity.this.finish();
        }, SPLASH_DISPLAY_TIMER);
    }
}
