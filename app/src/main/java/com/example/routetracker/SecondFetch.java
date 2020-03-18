package com.example.routetracker;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class SecondFetch {

    public List<List<HashMap<String, String>>> FetchData (String url){
        String data = "";
        List<List<HashMap<String, String>>> routes_data = null;
        try{
            data = downloadUrl(url);
            routes_data = fetch_routes_data(data);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return routes_data;
    }

    private List<List<HashMap<String, String>>> fetch_routes_data(String s){
        JSONObject jObject;
        List<List<HashMap<String, String>>> routes_data = null;
        try {
            jObject = new JSONObject(s);
            DataParser_DD parser = new DataParser_DD();

            routes_data = parser.parse_data(jObject);
            Log.d("TEST3!!!!!!!!!!!!!!!", String.valueOf(routes_data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes_data;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("mylog", "Downloaded URL: " + data.toString());
            br.close();
        } catch (Exception e) {
            Log.d("mylog", "Exception downloading URL: " + e.toString());
        } finally {
            //iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
