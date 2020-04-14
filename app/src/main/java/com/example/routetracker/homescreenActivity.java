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
import com.google.maps.GeoApiContext;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static java.lang.String.valueOf;


public class homescreenActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback, PointsParser.FetchResponse {

    // Map
    private GoogleMap mMap;
    private MapView mMapView;
    private static final int DEFAULT_ZOOM = 15;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    // User Location
    private FusedLocationProviderClient fusedLocationClient;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private boolean mLocationPermissionGranted = false;
    private LocationCallback mLocationCallback;
    private MarkerOptions locMarker;
    private GeoApiContext mGeoApiContext = null;
    // Destination
    private List<Address> addresses;
    private String mCurrentLocality;
    private MarkerOptions destination;
    // Route
    private Polyline currentRouteLine;
    public static List<List<HashMap<String, String>>> routeDetails;
    private RouteDataItem currentRouteData;
    private ArrayList<Polyline> polyLineList = new ArrayList<>();
    public static volatile ArrayList<ArrayList<LatLng>> stepPoints;
    private ArrayList<RouteDataItem> routeDataList;
    // UI
    private ProgressBar progressBar;
    private FrameLayout mFrameLayout;
    private EditText mSearchText;
    private ListView addressList;



    AdapterView.OnItemClickListener addressListClick = (parent, view, position, id) -> {
        markLocation(position);
        EditText editText = findViewById(R.id.input_search);
        editText.getText().clear();
    };

    private void getDeviceLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
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
                                mCurrentLocality = adr.getLocality();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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

        getDirectionButtonClick();
        dropMarkerButton();

        mSearchText = findViewById(R.id.input_search);
        MapsInitializer.initialize(getApplicationContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

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

    private String getUrl(Location origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.getLatitude() + "," + origin.getLongitude();
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + "walking";
        // Alternative routes
        String alte = "&alternatives=true";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode + alte;
        // Output format
        String output = "json";
        // Building the url to the web service

        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
    }

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
            Button getDirection = findViewById(R.id.btnGetDirection);
            //TODO Add confirm route
            //TODO Add save route
            //TODO Start Route

            getDirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    routeDataList = new ArrayList<>();
                    Thread progressThread = new Thread();
                    progressThread.start();

                    //startForegroundService(view);
                    //startTimeTriggers(view);

                    //TODO Once a confirm route option is in then adapt and move this to it
                    //activeRoute = true;


                    //      //      //      //

                    if (destination != null) {
                        new FetchURL(homescreenActivity.this, progressBar).execute(getUrl(mCurrentLocation, destination.getPosition()), "walking");
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


    private void initSearchBar() {
        mSearchText.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE
                || event.getAction() == KeyEvent.ACTION_DOWN
                || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                searchLocate();

            }
            return false;
        });
    }

    private void searchLocate() {
        String searchInput = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(homescreenActivity.this);
        addresses = new ArrayList<>();
        try {
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
                    assert imm != null;
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
        }
    }

    public void markLocation(int listIndex) {
        Address address = addresses.get(listIndex);

        addressList = findViewById(R.id.addressList);
        addressList.setVisibility(View.GONE);

        LatLng addressLtLn = new LatLng(address.getLatitude(), address.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLng(addressLtLn));
        mMap.clear();
        destination = new MarkerOptions();
        mMap.addMarker(destination
                .position(addressLtLn).title(address.getAddressLine(0))
                .draggable(true));

        addresses.clear();
        destination = new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())).title("Location 2");
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
            initSearchBar();
        }

        // Remove Navigation & GPS pointer Google buttons
        mMap.getUiSettings().setMapToolbarEnabled(false);

    }

    public void onTaskDone(Object... values) {
        polyLineList.add(mMap.addPolyline((PolylineOptions) values[0]));
    }


    public void highlightRoute(Integer selectedRouteIndex, RouteDataItem selectedRouteData){

        currentRouteData = selectedRouteData;
        currentRouteLine = polyLineList.get(selectedRouteIndex);
        for(int i = 0 ; i < polyLineList.size(); i++) {
            if(i != selectedRouteIndex)
                polyLineList.get(i).remove();
        }
        mFrameLayout.setVisibility(View.GONE);
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

            routeDataList.add(new RouteDataItem(counter, crimeCount, distance, duration, 0));
            counter++;
        }

        Collections.sort(routeDataList, (o1, o2) -> o1.getCrimeCount() - o2.getCrimeCount());
        Log.d("NUMBER OF ROUTES", String.valueOf(routeDataList.size()));

        if (routeDataList.size() == 3) {
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
            polyLineList.get(routeDataList.get(0).getID()-1).setColor(Color.GREEN);
            Log.d("ROUTECOLOR", valueOf(routeDataList.get(0).getID()-1));
            polyLineList.get(routeDataList.get(1).getID()).setColor(Color.RED);
            Log.d("ROUTECOLOR", valueOf(routeDataList.get(1).getID()-1));
        }
        else if (routeDataList.size() == 1) {
            routeDataList.get(0).setImage(R.drawable.ic_route_green);
            Log.d("route", polyLineList.get(routeDataList.get(0).getID()-1).toString());
            Log.d("route", polyLineList.get(0).toString());
            Log.d("routeID", String.valueOf(routeDataList.get(0).getID()-1));

            polyLineList.get(0).setColor(Color.GREEN);
        }

        homescreenActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, RoutesFragment.newInstance(getApplicationContext(), routeDataList, homescreenActivity.this)).commit();
        mFrameLayout = findViewById(R.id.frameLayout);
        mFrameLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private static Handler disconnectHandler = new Handler(msg -> {
        return true;
    });



    public void startTimeTriggers(View v) {
        long time = 0;
        Intent triggersIntent = new Intent(this, TimeTriggerService.class);
        triggersIntent.putExtra("timeID", time);
        startService(triggersIntent);
    }

    public void stopTimeTriggers(View v) {
        Intent triggersIntent = new Intent(this, TimeTriggerService.class);
        stopService(triggersIntent);
    }

    public void startForegroundService(View v) {
        Intent serviceIntent = new Intent(this, SensorService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        }
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopNotificationService(View v) {
        Intent serviceIntent = new Intent(this, SensorService.class);
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

    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();

        startLocationUpdates();
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



}

