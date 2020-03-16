package com.example.routetracker;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

public class CrimeCollector extends AsyncTask<List<LatLng>,Void, Integer> {

    private List<LatLng> crimepoints;
    private Calendar currentDate = Calendar.getInstance();
    public int totalCrimeCount = 0;
    private ArrayList<Integer> streetIDs = new ArrayList<>();

    public CrimeCollector(){ }

    @Override
    protected Integer doInBackground(List<LatLng>... lists) {
        crimepoints = lists[0];

        int month = currentDate.get(Calendar.MONTH);
        if (month == 0 || month == 11)
            month++;

        for (LatLng point : crimepoints) {
            String sURL = "https://data.police.uk/api/crimes-at-location?date=" + currentDate.get(Calendar.YEAR) + "-" + month
                    + "&lat=" + point.latitude + "&lng=" + point.longitude;

            URL url = null;
            try {
                url = new URL(sURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            URLConnection request = null;
            try {
                request = url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                request.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            JsonParser jp = new JsonParser();
            JsonElement root = null;
            try {
                root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            JsonObject rootObj = null;

            if (root instanceof JsonObject) {
                rootObj = root.getAsJsonObject();
            }

            if (rootObj != null)
                countCrimes(rootObj);
        }

        return totalCrimeCount;
    }

    private void countCrimes(JsonObject crimeJSON){
        JsonArray crimeLocations = crimeJSON.getAsJsonArray("routes");
        for (int i = 0; i < crimeLocations.size(); i++) {
            int id = crimeLocations.get(i).getAsJsonObject().get("street").getAsJsonObject().get("id").getAsInt();
            if (!streetIDs.contains(id)) {
                streetIDs.add(id);
                totalCrimeCount++;
            }
        }

    }
}
