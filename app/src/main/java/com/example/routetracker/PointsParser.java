package com.example.routetracker;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PointsParser extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    TaskLoadedCallback taskCallback;
    String directionMode = "walking";
    int[] ColourList = {Color.MAGENTA, Color.GREEN, Color.YELLOW, Color.RED, Color.BLUE};

    public PointsParser(Context mContext, String directionMode) {
        this.taskCallback = (TaskLoadedCallback) mContext;
        this.directionMode = directionMode;
    }

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            Log.d("mylog", jsonData[0].toString());
            DataParser parser = new DataParser();
            Log.d("mylog", parser.toString());

            // Starts parsing data
            routes = parser.parse(jObject);
            Log.d("mylog", "Executing routes");
            Log.d("mylog", routes.toString());

        } catch (Exception e) {
            Log.d("mylog", e.toString());
            e.printStackTrace();
        }
        return routes;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points;
        ArrayList<LatLng> fastestPoints;
        ArrayList<PolylineOptions> lineOptions = new ArrayList<>();
        int totalRoutes = 0;
        PolylineOptions fastestRoute = new PolylineOptions();
        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            totalRoutes = totalRoutes + 1;
            points = new ArrayList<>();
            fastestPoints = new ArrayList<>();
            PolylineOptions lineOption = new PolylineOptions();
            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);
            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                HashMap<String, String> fastest = path.get(0);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                double lat1 = Double.parseDouble(fastest.get("lat"));
                double lng1 = Double.parseDouble(fastest.get("lng"));
                LatLng position = new LatLng(lat, lng);
                LatLng fastestLatLong = new LatLng(lat1, lng1);
                points.add(position);
                fastestPoints.add(fastestLatLong);

            }
            // Adding all the points in the route to LineOptions

            lineOption.addAll(points);
            fastestRoute.addAll(fastestPoints);
            lineOptions.add(lineOption);



            }

        Log.d("TOTAL ROUTES: ", String.valueOf(totalRoutes));

        for (int i = 0; i < lineOptions.size(); i++) {
            lineOptions.get(i).width(10);
            lineOptions.get(i).color(ColourList[i]);
            taskCallback.onTaskDone(lineOptions.get(i));
        }


    /*
        if (totalRoutes == 1) {
            lineOptions.width(10);
            lineOptions.color(Color.MAGENTA);
        } else {
            lineOptions.width(10);
            lineOptions.color(Color.GRAY);
            //print all of the grey lines first
            taskCallback.onTaskDone(lineOptions);
            Log.d("mylog", "onPostExecute lineoptions decoded");

            //taskCallback.onTaskDone(lineOptions);

     */
        }
        //print the fastest route last so the colour shows
    /*
        fastestRoute.color(Color.MAGENTA);
        fastestRoute.width(10);
        taskCallback.onTaskDone(fastestRoute);

     */

        // Drawing polyline in the Google Map for the i-th route
        /*if (lineOptions != null) {
            //mMap.addPolyline(lineOptions);
            taskCallback.onTaskDone(lineOptions);

        } else {
            Log.d("mylog", "without Polylines drawn");
        }*/
}
