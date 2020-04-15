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

    public RouteDataItem(int inRouteID, int inCrimeCount, String inDistance, String inTime, int inImage, PolylineOptions inPolyline) {

        routeID = inRouteID;
        crimeCount = inCrimeCount;
        distance = inDistance;
        time = inTime;
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
}
