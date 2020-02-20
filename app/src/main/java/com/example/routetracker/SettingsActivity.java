package com.example.routetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        configureBackButtonSettings();
    }

    private void configureBackButtonSettings(){
        Button backButton = findViewById(R.id.buttonCreateUserBack);
        backButton.setOnClickListener(v -> finish());
    }
}
