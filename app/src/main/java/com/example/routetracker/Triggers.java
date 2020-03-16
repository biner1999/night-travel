package com.example.routetracker;
import java.util.Timer;
import java.util.TimerTask;



public class Triggers {

    // First Time Trigger //
    public void FirstTriggerStart(long time ) {
        TimerTask task = new TimerTask() {
            public void run() {
                System.out.println("PLACE CALL FOR THE FIRST TRIGGER HERE");
            }
        };
        Timer timer = new Timer("Timer");

        long delay = Math.round(time*1.25) + 5000;
        timer.schedule(task, delay);
    }
}
