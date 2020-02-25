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
    Button b1;
    DatabaseFunctions db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DatabaseFunctions(this);

        e2 = findViewById(R.id.editText3);
        b1 = findViewById(R.id.button);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = e2.getText().toString();
                Boolean Chkpass = db.checkpassword(password);
                if(Chkpass==true){
                    Toast.makeText(getApplicationContext(),"Successfully logged in",Toast.LENGTH_SHORT).show();
                    Intent nextScreen = new Intent(LoginActivity.this, homescreenActivity.class);
                    startActivity(nextScreen);}
                else
                    Toast.makeText(getApplicationContext(),"Failed to log in",Toast.LENGTH_SHORT).show();


            }
        });
    }
}


