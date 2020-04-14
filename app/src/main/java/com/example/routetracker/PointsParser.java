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
import java.util.concurrent.ExecutionException;


public class PointsParser extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    TaskLoadedCallback taskCallback;
    String directionMode = "walking";
    public FetchResponse delegate = null;

    public PointsParser(Context mContext, String directionMode) {
        this.taskCallback = (TaskLoadedCallback) mContext;
        this.directionMode = directionMode;
        delegate = (FetchResponse) mContext;
    }

    public interface FetchResponse {
        void listRoutes() throws ExecutionException, InterruptedException;
    }

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;
        //List<List<HashMap<String>>> dist = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            Log.d("TEST2!!!!!!!!!!!", jObject.toString());
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
        PolylineOptions lineOptions = null;
        int totalRoutes = 0;

        PolylineOptions fastestRoute = new PolylineOptions();
        PolylineOptions secondRoute = new PolylineOptions();

        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            totalRoutes = totalRoutes + 1;
            points = new ArrayList<>();

            lineOptions = new PolylineOptions();
            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);
            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {



                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                points.add(position);


            }
            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);


            if (totalRoutes == 1) {
                fastestRoute = lineOptions;
                fastestRoute.width(10);
                fastestRoute.color(Color.GREEN);
                //taskCallback.onTaskDone(fastestRoute);

            } else if (totalRoutes == 2) {
                secondRoute = lineOptions;
                secondRoute.width(10);
                secondRoute.color(Color.BLUE);
                //taskCallback.onTaskDone(secondRoute);
            }
            else{
                lineOptions.width(10);
                lineOptions.color(Color.GRAY);
                //taskCallback.onTaskDone(lineOptions);
            }
            Log.d("mylog", "onPostExecute lineoptions decoded");

            //taskCallback.onTaskDone(lineOptions);
        }
        taskCallback.onTaskDone(fastestRoute);

        taskCallback.onTaskDone(secondRoute);

        if (lineOptions != null)
            taskCallback.onTaskDone(lineOptions);



        try {
            delegate.listRoutes();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //print the fastest route last so the colour shows
        //taskCallback.onTaskDone(fastestRoute);

        // Drawing polyline in the Google Map for the i-th route
        /*if (lineOptions != null) {
            //mMap.addPolyline(lineOptions);
            taskCallback.onTaskDone(lineOptions);

        } else {
            Log.d("mylog", "without Polylines drawn");
        }*/
    }
}
