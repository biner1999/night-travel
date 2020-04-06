package com.example.routetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.String.valueOf;


public class homescreenActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback, PointsParser.FetchResponse {

    private GoogleMap mMap;
    private MapView mMapView;
    private FusedLocationProviderClient fusedLocationClient;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private boolean mLocationPermissionGranted = false;
    private LocationCallback mLocationCallback;
    private static final int DEFAULT_ZOOM = 15;
    private MarkerOptions locMarker;
    private GeoApiContext mGeoApiContext = null;
    private static final String TAG = "UserListFragment";
    private List<Address> addresses;
    private Marker destMarker;
    private String mCurrentLocality;
    private ProgressBar progressBar;

    private boolean activeRoute = false;
    public static final long DISCONNECT_TIMEOUT = 5000;//300000; // 5 min = 5 * 60 * 1000 ms
    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SensorEventListener gyroscopeEventListener;


    //ciprian
    private MarkerOptions destination;
    private Polyline currentPolyline;
    Button getDirection;
    public static List<List<HashMap<String, String>>> routeDetails;
    //ciprian



    private ArrayList<Polyline> polyLineList = new ArrayList<>();

    private List<List<HashMap<String, String>>> duration_time;

    public static volatile ArrayList<ArrayList<LatLng>> stepPoints;

    private ArrayList<RouteDataItem> routeDataList = new ArrayList<>();

    //widgets
    private EditText mSearchText;
    private ListView addressList;

    //other
    AdapterView.OnItemClickListener addressListClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            markLocation(position);
            EditText editText = findViewById(R.id.input_search);
            editText.getText().clear();
        }
    };

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private void getDeviceLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mCurrentLocation = location;
                    LatLng locationCoords = new  LatLng(mCurrentLocation.getLatitude(),
                            mCurrentLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationCoords, DEFAULT_ZOOM));
                    locMarker.position(locationCoords);

                    Geocoder geocoder = new Geocoder(homescreenActivity.this, Locale.ENGLISH);
                    try {
                        List<Address> currentAddress = geocoder.getFromLocation(mCurrentLocation.getLatitude(),
                                mCurrentLocation.getLongitude(), 20);
                        if (currentAddress.size() > 0) {
                            for (Address adr : currentAddress) {
                                if (adr.getLocality() != null && adr.getLocality().length() > 0) {
                                    Log.d(TAG, "getDeviceLocation SUBLOCALITY : " + adr.getLocality());
                                    mCurrentLocality = adr.getLocality();
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    protected void createLocationRequest() {
        // Set up location retrieval - requests updates with
        // HIGH ACCURACY and at interval of 5000ms
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setPriority(100);
    }

    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper());
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_homescreen);

        //Initialise Buttons
        settingsView();
        savedDestinationsView();

        //ciprian
        getDirectionButtonClick();
        dropMarkerButton();

        mSearchText = findViewById(R.id.input_search);
        MapsInitializer.initialize(getApplicationContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);


        
        //ciprian

        locMarker = new MarkerOptions();
        locMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        // Obtain the Map View and register the callback
        Bundle mapViewBundle = null;

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null)
                    return;

                for (Location location : locationResult.getLocations()) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),
                            location.getLongitude())));
                    LatLng locationCoords = new  LatLng(location.getLatitude(),
                            location.getLongitude());
                    locMarker.position(locationCoords);

                }

                sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

                if (gyroscopeSensor == null){
                    Toast.makeText(homescreenActivity.this, "The device has no Gyroscope", Toast.LENGTH_SHORT).show();
                    finish();
                }

                gyroscopeEventListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent sensorEvent) {
                        if (sensorEvent.values[2] > 0.5f){
                            resetDisconnectTimer();
                            Toast.makeText(homescreenActivity.this, "Detected", Toast.LENGTH_SHORT).show();
                            System.out.println("DETECTED ");

                        } else if (sensorEvent.values[2] < -0.5f){
                            resetDisconnectTimer();
                            Toast.makeText(homescreenActivity.this, "Detected", Toast.LENGTH_SHORT).show();
                            System.out.println("DETECTED ");

                        }
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                };

            }
        };

        if(savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

        if(mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }


    }

    //ciprian
    private String getUrl(Location origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.getLatitude() + "," + origin.getLongitude(); //String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Alternative routes
        String alte = "&alternatives=true";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode + alte;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);

        return url;
    }

    //ciprian

    private List<HashMap<String, String>> getRouteDetails(List<List<HashMap<String, String>>> details, Integer route){

        List<HashMap<String, String>> test = null;
        for (int i = 0; i < details.size(); i++) {
            // Fetching i-th route
            List<HashMap<String, String>> path = details.get(route);
            test = path;
            // Fetching all the points in i-th route
            //HashMap<String, String> point = path.get(route);
            //String duration = point.get("duration");
            //String distance = point.get("distance");
            }
        return test;
    }


        private void getDirectionButtonClick(){
            getDirection = findViewById(R.id.btnGetDirection);
            //TODO Add confirm route
            //TODO Add save route
            //TODO Start Route

            activeRoute = true;
            startDisconnectTimer();

            getDirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Thread progressThread = new Thread();
                    progressThread.start();

                    //startForegroundService(view);
                    //startTriggers(view);


                    //      //      //      //

                    if (destination != null) {
                        new FetchURL(homescreenActivity.this, progressBar).execute(getUrl(mCurrentLocation, destination.getPosition(), "walking"), "walking");
                        Log.d("TEST4:", String.valueOf(duration_time));
                    }

                    else {
                        Toast noDestinationToast = Toast.makeText(getApplicationContext(),
                                "No Destination Selected", Toast.LENGTH_LONG);
                        noDestinationToast.show();
                    }
                }
            });
        }

    private void settingsView(){
        Button btnSettings = findViewById(R.id.button_settings);
        btnSettings.setOnClickListener(v -> startActivity(new Intent(homescreenActivity.this, SettingsActivity.class)));
    }

    private void savedDestinationsView(){
        Button btnSavedDestinations = findViewById(R.id.buttonSavedDestinations);
        btnSavedDestinations.setOnClickListener(v -> startActivity(new Intent(homescreenActivity.this, SavedDestinationActivity.class)));
    }

    private void dropMarkerButton(){
        ToggleButton mDropMarkerBtn = findViewById(R.id.dropMarker);
        mDropMarkerBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mDropMarkerBtn.setBackgroundColor(0xD5B1B1B1);
                mMap.setOnMapClickListener(latLng -> {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                    mMap.clear();
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    mMap.addMarker(markerOptions);
                    destination = markerOptions;
                    mDropMarkerBtn.setChecked(false);
                });
            } else {
                mDropMarkerBtn.setBackgroundColor(0xD5FFFFFF);
                mMap.setOnMapClickListener(latLng -> {
                    // Other map click listener code here
                });
            }
        });
    }


    private void init() {
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == KeyEvent.ACTION_DOWN
                    || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    searchLocate();

                }
                return false;
            }
        });
    }

    private void searchLocate() {
        String searchInput = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(homescreenActivity.this);
        addresses = new ArrayList<>();
        try {
            Log.e(TAG, "mCurrentLocality : " + mCurrentLocality);
            addresses = geocoder.getFromLocationName(searchInput + mCurrentLocality, 5);
            if(addresses.size() > 0) {
                String[] addressStrings = new String[addresses.size()];

                for (int i = 0; i < addresses.size(); i++) {
                    Address nextAddress = addresses.get(i);
                    addressStrings[i] = nextAddress.getAddressLine(0);
                }

                addressList = findViewById(R.id.addressList);
                ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, addressStrings);
                addressList.setAdapter(addressAdapter);

                addressList.setVisibility(View.VISIBLE);
                addressList.setOnItemClickListener(addressListClick);

                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                }
            }
            else {
                Toast noResultsToast = Toast.makeText(homescreenActivity.this,
                        "No results found.", Toast.LENGTH_SHORT);
                noResultsToast.show();
            }
        }

        catch (IOException e) {
            Log.e(TAG, "searchLocate: IOException: " + e.getMessage());
        }
    }

    public MarkerOptions markLocation(int listIndex) {
        Address address = addresses.get(listIndex);
        Log.d(TAG, "markLocation: found a location: " + address.toString());

        if (destMarker != null) {
            destMarker.remove();
        }

        addressList = findViewById(R.id.addressList);
        addressList.setVisibility(View.GONE);

        LatLng addressLtLn = new LatLng(address.getLatitude(), address.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLng(addressLtLn));
        mMap.clear();
        destMarker = mMap.addMarker( new MarkerOptions()
                .position(addressLtLn).title(address.getAddressLine(0))
                .draggable(true));

        addresses.clear();
        //ciprian
        destination = new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())).title("Location 2");
        return destination;
        //ciprian

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            finish();
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            createLocationRequest();
            getDeviceLocation();
            init();
        }

        // Remove Navigation & GPS pointer Google buttons
        mMap.getUiSettings().setMapToolbarEnabled(false);

    }

    public void onTaskDone(Object... values) {

        polyLineList.add(mMap.addPolyline((PolylineOptions) values[0]));
        Log.d("DDDDDDDDDDD", String.valueOf(polyLineList));

    }

    public void listRoutes() throws ExecutionException, InterruptedException {
        int counter = 1;

        for(ArrayList<LatLng> step : stepPoints) {


            String distance = null;
            String duration = null;
            List<HashMap<String, String>> details = getRouteDetails(routeDetails, counter-1);
            HashMap<String, String> point = details.get(0);
            duration = point.get("duration");
            distance = point.get("distance");

            CrimeCollector crimeCollector = new CrimeCollector();
            int crimeCount = crimeCollector.execute(step).get();

            Log.d("Route crimes " + counter, String.valueOf(crimeCount));


            routeDataList.add(new RouteDataItem(counter, crimeCount, distance, duration, 0));
            counter++;
        }

        Collections.sort(routeDataList, (o1, o2) -> o1.getCrimeCount() - o2.getCrimeCount());

        if (routeDataList.size() == 3) {
            Log.d("linelistsize", String.valueOf(polyLineList.size()));
            routeDataList.get(0).setImage(R.drawable.ic_route_green);
            routeDataList.get(1).setImage(R.drawable.ic_route_yellow);
            routeDataList.get(2).setImage(R.drawable.ic_route_crimson);
            polyLineList.get(routeDataList.get(0).getID()-1).setColor(Color.GREEN);
            polyLineList.get(routeDataList.get(1).getID()-1).setColor(Color.YELLOW);
            polyLineList.get(routeDataList.get(2).getID()-1).setColor(Color.RED);
        }
        else if (routeDataList.size() == 2) {
            routeDataList.get(0).setImage(R.drawable.ic_route_green);
            routeDataList.get(1).setImage(R.drawable.ic_route_crimson);
            Log.d("linelistsize", String.valueOf(polyLineList.size()));
            polyLineList.get(routeDataList.get(0).getID()-1).setColor(Color.GREEN);
            Log.d("ROUTECOLOR", String.valueOf(routeDataList.get(0).getID()-1));
            polyLineList.get(routeDataList.get(1).getID()).setColor(Color.RED);
            Log.d("ROUTECOLOR", String.valueOf(routeDataList.get(1).getID()-1));
        }
        else if (routeDataList.size() == 1) {
            Log.d("linelistsize", String.valueOf(polyLineList.size()));
            routeDataList.get(0).setImage(R.drawable.ic_route_green);
            polyLineList.get(routeDataList.get(0).getID()+1).setColor(Color.GREEN);
        }

        homescreenActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, RoutesFragment.newInstance(getApplicationContext(), routeDataList)).commit();
        FrameLayout mFrameLayout = findViewById(R.id.frameLayout);
        mFrameLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private static Handler disconnectHandler = new Handler(msg -> {
        // todo
        return true;
    });

    private static Runnable disconnectCallback = () -> {
        // Perform any required operation on disconnect
        System.out.println("Disconnect");

    };

    public void resetDisconnectTimer(){
        System.out.println("Reset Disconnect Timer");
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        System.out.println("Stop Disconnect Timer");
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    public void startDisconnectTimer(){
        System.out.println("Start Disconnect Timer");
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }
    public void startTriggers(View v) {
        int time = 5;
        Intent triggersIntent = new Intent(this, TriggerService.class);
        triggersIntent.putExtra("timeID", time);
        startService(triggersIntent);
    }

    public void stopTriggers(View v) {
        Intent triggersIntent = new Intent(this, TriggerService.class);
        stopService(triggersIntent);
    }

    public void startForegroundService(View v) {
        Intent serviceIntent = new Intent(this, NotificationsService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        }
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopNotificationService(View v) {
        Intent serviceIntent = new Intent(this, NotificationsService.class);
        stopService(serviceIntent);
    }







    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();

        startLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        //sensorManager.unregisterListener(gyroscopeEventListener);

    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();

        startLocationUpdates();
        //sensorManager.registerListener(gyroscopeEventListener,gyroscopeSensor,SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mMapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onUserInteraction(){
        System.out.println("User Interaction");
        resetDisconnectTimer();
    }



}

