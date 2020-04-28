package com.example.routetracker;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

class RouteDataItem {

    private int crimeCount;
    private String distance;
    private String time;
    private int image;
    private PolylineOptions polyline;
    private long numTime;
    private long startTime;

    RouteDataItem(int inRouteID, int inCrimeCount, String inDistance, String inTime, String inNumTime, long inStartTime, int inImage, PolylineOptions inPolyline) {

        crimeCount = inCrimeCount;
        distance = inDistance;
        time = inTime;
        numTime = Long.parseLong(inNumTime);
        startTime = inStartTime;
        image = inImage;
        polyline = inPolyline;

    }

    int getCrimeCount() {return crimeCount;}
    String getRouteDistance() {return distance;}
    String getRouteTime() {return time;}
    int getImage() {return image;}
    PolylineOptions getPolyline() {return polyline;}
    void setImage(int inImage) {image = inImage;}
    long getNumTime() {return numTime;}
    long getStartTime() {return startTime;}
}
