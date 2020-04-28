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

    private Calendar currentDate = Calendar.getInstance();
    private int totalCrimeCount = 0;
    private ArrayList<Integer> crimeIDs = new ArrayList<>();

    CrimeCollector(){ }

    @SafeVarargs
    @Override
    protected final Integer doInBackground(List<LatLng>... lists) {
        List<LatLng> crimepoints = lists[0];

        int month = currentDate.get(Calendar.MONTH)-1;
        if (month == 0 || month == 11)
            month++;

        Log.d("MONTH:", String.valueOf(month));

        for (LatLng point : crimepoints) {
            String sURL = "https://data.police.uk/api/crimes-at-location?date=" + currentDate.get(Calendar.YEAR) + "-" + month
                    + "&lat=" + point.latitude + "&lng=" + point.longitude;

            //Log.d("HTTPURL:", sURL);

            URL url = null;
            try {
                url = new URL(sURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            URLConnection request = null;
            try {
                assert url != null;
                request = url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                assert request != null;
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
            JsonArray rootArray = null;


            assert root != null;
            if (root.isJsonObject())
                rootObj = root.getAsJsonObject();

            else if (root.isJsonArray())
                rootArray = root.getAsJsonArray();

            if (rootObj != null)
                countCrimes(rootObj);

            if (rootArray != null)
                countCrimes(rootArray);
        }

        return totalCrimeCount;
    }

    private void countCrimes(JsonObject crimeJSON){
        Log.d("CountCrimes: ", "Call to count crimes");
        JsonArray jCrimeIDs = crimeJSON.getAsJsonArray("id");
        for (int i = 0; i < jCrimeIDs.size(); i++) {
            int id = jCrimeIDs.get(i).getAsInt();
            if (!crimeIDs.contains(id)) {
                crimeIDs.add(id);
                totalCrimeCount++;
            }
        }
    }

    private void countCrimes(JsonArray crimeJSON){
        Log.d("CountCrimes: ", "Call to count crimes");
        JsonArray jCrimeIDs = crimeJSON.getAsJsonArray();
        for (int i = 0; i < jCrimeIDs.size(); i++) {
            int id = jCrimeIDs.get(i).getAsJsonObject().get("id").getAsInt();
            if (!crimeIDs.contains(id)) {
                crimeIDs.add(id);
                totalCrimeCount++;
            }
        }
    }
}
