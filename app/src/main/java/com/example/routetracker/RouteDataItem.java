package com.example.routetracker;

public class RouteDataItem {

    private int routeID;
    private int crimeCount;
    private String distance;
    private String time;
    private int image;

    public RouteDataItem(int inRouteID, int inCrimeCount, String inDistance, String inTime, int inImage) {

        routeID = inRouteID;
        crimeCount = inCrimeCount;
        distance = inDistance;
        time = inTime;
        image = inImage;

    }

    public int getID() {return routeID;}
    public int getCrimeCount() {return crimeCount;}
    public String getRouteDistance() {return distance;}
    public String getRouteTime() {return time;}
    public int getImage() {return image;}
}
