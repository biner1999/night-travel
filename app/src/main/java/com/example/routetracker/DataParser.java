package com.example.routetracker;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DataParser {
    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;

        ArrayList<ArrayList<LatLng>> routeSteps = new ArrayList<>();
        List<List<HashMap<String, String>>> routeData = new ArrayList<>();


        try {
            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                String jDuration;
                String jDistance;
                ArrayList<LatLng> stepPoints = new ArrayList<>();
                List durationDistance = new ArrayList<>();
                List path = new ArrayList<>();
                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    HashMap<String, String> hashMap = new HashMap<>();

                    jDuration = (String) ((JSONObject) ((JSONObject) jLegs.get(j)).get("duration")).get("text");
                    jDistance = (String) ((JSONObject) ((JSONObject) jLegs.get(j)).get("distance")).get("text");

                    hashMap.put("duration",jDuration);
                    hashMap.put("distance", jDistance);
                    durationDistance.add(hashMap);

                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        //Log.d("STEPSTARTLOCATION", ((JSONObject) jSteps.get(k)).get("start_location").toString());
                        //Log.d("STEPSTARTLOCATIONTYPE!!", ((JSONObject) jSteps.get(k)).get("start_location").getClass().toString());

                        JSONObject thisStepPoint = (JSONObject) ((JSONObject) jSteps.get(k)).get("start_location");
                        stepPoints.add(new LatLng(Double.parseDouble(thisStepPoint.optString("lat")), Double.parseDouble(thisStepPoint.optString("lng"))));


                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString((list.get(l)).latitude));
                            hm.put("lng", Double.toString((list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
                routeSteps.add(stepPoints);
                routeData.add(durationDistance);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }

        homescreenActivity.stepPoints = routeSteps;
        homescreenActivity.routeDetails = routeData;

        return routes;
    }


    /**
     * Method to decode polyline points
     * Courtesy : https://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}