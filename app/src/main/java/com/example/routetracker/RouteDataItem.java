package com.example.routetracker;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class RouteDataItem {

    private int routeID;
    private int crimeCount;
    private String distance;
    private String time;
    private int image;
    private PolylineOptions polyline;
    private long numTime;
    private long startTime;

    public RouteDataItem(int inRouteID, int inCrimeCount, String inDistance, String inTime, String inNumTime, long inStartTime, int inImage, PolylineOptions inPolyline) {

        routeID = inRouteID;
        crimeCount = inCrimeCount;
        distance = inDistance;
        time = inTime;
        numTime = Long.parseLong(inNumTime);
        startTime = inStartTime;
        image = inImage;
        polyline = inPolyline;

    }

    public int getID() {return routeID;}
    public int getCrimeCount() {return crimeCount;}
    public String getRouteDistance() {return distance;}
    public String getRouteTime() {return time;}
    public int getImage() {return image;}
    public PolylineOptions getPolyline() {return polyline;}
    public void setImage(int inImage) {image = inImage;}
    public long getNumTime() {return numTime;}
    public long getStartTime() {return startTime;}
}
