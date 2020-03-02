package com.example.routetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.PasswordAuthentication;

public class LoginActivity extends AppCompatActivity {
    EditText e2;
    Button login, newuser, DEBUG;
    DatabaseFunctions db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DatabaseFunctions(this);

        e2 = findViewById(R.id.login_pin);
        login = findViewById(R.id.login_btn);
        newuser = findViewById(R.id.newuser_btn);
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
        if(!checkUserExists()) {
            newuser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent newUserScreen = new Intent(LoginActivity.this, CreateUserActivity.class);
                    startActivity(newUserScreen);
                }
            });
        } else {
            newuser.setVisibility(View.GONE);
        }

        DEBUG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent DebugScreen = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(DebugScreen);
            }
        });
    }

    private boolean checkUserExists(){ // NEEDS IMPLEMENTING
       return false;
    }

}


