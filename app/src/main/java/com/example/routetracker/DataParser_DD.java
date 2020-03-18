package com.example.routetracker;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser_DD {
    public List<List<HashMap<String, String>>> parse_data(JSONObject jObject) {

        List<List<HashMap<String, String>>> duration_time = new ArrayList<>();
        JSONArray jRoutes;
        JSONArray jLegs;


        try {
            jRoutes = jObject.getJSONArray("routes");
            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");

                String jDuration;
                String jDistance;

                List path = new ArrayList<>();
                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {

                    HashMap<String, String> hm = new HashMap<>();
                    jDuration = (String) ((JSONObject) ((JSONObject) jLegs.get(j)).get("duration")).get("text");
                    jDistance = (String) ((JSONObject) ((JSONObject) jLegs.get(j)).get("distance")).get("text");
                    hm.put("duration",jDuration);
                    hm.put("distance", jDistance);
                    path.add(hm);
                    Log.d("mylog", "Executing duration and distance");
                    Log.d("mylog", jDuration + " " + jDistance);


                }
                duration_time.add(path);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return duration_time;
    }
}