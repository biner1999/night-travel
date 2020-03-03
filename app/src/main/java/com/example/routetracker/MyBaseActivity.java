package com.example.routetracker;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class MyBaseActivity extends Activity {

    public static final long DISCONNECT_TIMEOUT = 5000;//300000; // 5 min = 5 * 60 * 1000 ms


    private static Handler disconnectHandler = new Handler(msg -> {
        // todo
        return true;
    });

    private static Runnable disconnectCallback = () -> {
        // Perform any required operation on disconnect
        System.out.println("Disconnect");




    };

    public void resetDisconnectTimer(){
        System.out.println("Reset Disconnect Timer");
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        System.out.println("Stop Disconnect Timer");
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    public void startDisconnectTimer(){
        System.out.println("Start Disconnect Timer");
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }


    @Override
    public void onUserInteraction(){
        System.out.println("User Interaction");
        resetDisconnectTimer();
    }

    @Override
    public void onStart(){
        super.onStart();
        System.out.println("Start");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("On Resume");

        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("On Stop");

        //stopDisconnectTimer();
    }
}
