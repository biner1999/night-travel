package com.example.routetracker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.GeoApiContext;


import com.google.maps.android.PolyUtil;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.service.notification.StatusBarNotification;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
    private PolylineOptions currentRouteLine;
    public static List<List<HashMap<String, String>>> routeDetails;
    public RouteDataItem currentRouteData;
    private ArrayList<PolylineOptions> polyLineList;
    private ArrayList<Polyline> polyLineVisibleList;
    public static volatile ArrayList<ArrayList<LatLng>> stepPoints;
    private ArrayList<RouteDataItem> routeDataList;
    // UI
    private ProgressBar progressBar;
    private FrameLayout mFrameLayout;
    private EditText mSearchText;
    private ListView addressList;
    private long backPressedTime = 0;

    Handler handler = new Handler();


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




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_homescreen);

        //Initialise Buttons
        settingsView();
        savedDestinationsView();

        getDirectionButtonClick();
        endRoute();
        dropMarkerButton();
        addSavedDestinations();

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
                mCurrentLocation = locationResult.getLastLocation();
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
        displayTutorial();
    }

    private void displayTutorial() {
        boolean firstLogin = true; //TESTING
        ArrayList<AlertDialog> popups = new ArrayList<>();
        AtomicBoolean running = new AtomicBoolean(true);
        AtomicInteger popupIndex = new AtomicInteger(0);
        ImageView arrow1_1, arrow1_2, arrow2_1, arrow3_1;
        arrow1_1 = findViewById(R.id.tutArrow1_1);
        arrow1_2 = findViewById(R.id.tutArrow1_2);
        arrow3_1 = findViewById(R.id.tutArrow3_1);

        if (!firstLogin) {
            return;
        }

        // Popup 1
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Thanks for using Route Tracker! Would you like a quick tutorial?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                }).setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
            running.set(false);
        }).setOnDismissListener(dialog -> {
            if (!running.get())
                return;
            popupIndex.getAndIncrement();
            popups.get(popupIndex.get()).show();
        });
        AlertDialog pop1 = builder.create();
        popups.add(pop1);
        // Popup 2
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Route Tracker is an app designed to keep you safe when travelling alone.\nIt does this by tracking you on your journey and " +
                "informing your chosen emergency contact and/or the police when anomalies are detected")
                .setPositiveButton("Continue", (dialog, which) -> {
                    dialog.dismiss();
                }).setOnDismissListener(dialog -> {
            popupIndex.getAndIncrement();
            if (popupIndex.get() < 8) {
                popups.get(popupIndex.get()).show();
                if (popupIndex.get() == 3) {
                    arrow1_1.setVisibility(View.VISIBLE);
                    arrow1_2.setVisibility(View.VISIBLE);
                }
                else {
                    arrow1_1.setVisibility(View.GONE);
                    arrow1_2.setVisibility(View.GONE);
                }
                if (popupIndex.get() == 6) {
                    arrow3_1.setVisibility(View.VISIBLE);
                }
                else {
                    arrow3_1.setVisibility(View.GONE);
                }

            }
        });;
        AlertDialog pop2 = builder.create();
        popups.add(pop2);
        // Popup 3
        builder.setMessage("Route Tracker will also inform you of the safest routes to your destination based on recent Police crime data");
        AlertDialog pop3 = builder.create();
        popups.add(pop3);
        // Popup 4
        builder.setMessage("You are represented by the blue dot on the map.\nTo select a destination, use the search bar or the Marker Dropper button marked by the arrows");
        AlertDialog pop4 = builder.create();
        popups.add(pop4);
        // Popup 5
        builder.setMessage("When you have selected a destination, you can view possible routes to that destination with the 'Get Directions' button.\n" +
                "Once pressed, Route Tracker will retrieve crime data and display up to three possible routes to choose from. Select your desired route and begin your journey!");
        AlertDialog pop5 = builder.create();
        popups.add(pop5);
        // Popup 6
        builder.setMessage("If you deviate too far from your desired route, take too long to reach your destination or anomalous movements are detected on your device, " +
                "alerts will be triggered. This starts with an alarm sound from your device, " +
                "followed by a text message sent to your emergency contact and eventually the police with your location and description.");
        AlertDialog pop6 = builder.create();
        popups.add(pop6);
        // Popup 7
        builder.setMessage("You can also save destinations for later with the 'Add Destination' button marked by the arrow");
        AlertDialog pop7 = builder.create();
        popups.add(pop7);
        // Popup 8
        builder.setMessage("We hope you stay safe using Route Tracker!").setPositiveButton("Finish", (dialog, which) -> {
            dialog.dismiss();
        }).setOnDismissListener(dialog -> { });
        AlertDialog pop8 = builder.create();
        popups.add(pop8);

        popups.get(0).show();
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

        Button endRoute = findViewById(R.id.btnEndRoute);
        //TODO Add confirm route
        //TODO Add save route
        //TODO Start Route

        getDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                polyLineList = new ArrayList<>();
                polyLineVisibleList = new ArrayList<>();
                routeDataList = new ArrayList<>();
                Thread progressThread = new Thread();
                progressThread.start();


                //TODO Once a confirm route option is in then adapt and move this to it
                //activeRoute = true;


                //      //      //      //

                if (destination != null) {
                    new FetchURL(homescreenActivity.this, progressBar).execute(getUrl(mCurrentLocation, destination.getPosition()), "walking");
                    endRoute.setVisibility(View.VISIBLE);
                    getDirection.setVisibility(View.GONE);


                }

                else {
                    Toast noDestinationToast = Toast.makeText(getApplicationContext(),
                            "No Destination Selected", Toast.LENGTH_LONG);
                    noDestinationToast.show();
                }
            }
        });
    }

    private void endRoute() {
        Button endRoute = findViewById(R.id.btnEndRoute);
        Button getDirection = findViewById(R.id.btnGetDirection);
        endRoute.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //put those below in the code where the user gets to the destination and the route finishes
                stopForegroundService();
                stopNotificationsRestartService();
                stopTimeTriggersService();
                stopTimeLeftTriggerService();
                stopSensorTriggerService();
                mMap.clear();
                endRoute.setVisibility(View.GONE);
                getDirection.setVisibility(View.VISIBLE);
                destination = null;
            }
        });

    }
    private void settingsView(){
        Button btnSettings = findViewById(R.id.button_settings);
        btnSettings.setOnClickListener(v -> startActivity(new Intent(homescreenActivity.this, SettingsActivity.class)));
    }

    private void savedDestinationsView(){
        Button btnSavedDestinations = findViewById(R.id.buttonSavedDestinations);
        SavedDestinationActivity.homescreen = homescreenActivity.this;
        btnSavedDestinations.setOnClickListener(v -> startActivity(new Intent(homescreenActivity.this, SavedDestinationActivity.class)));
    }

    private void addSavedDestinations(){
        Button btnAddDestination = findViewById(R.id.addDestinationBtn);
        btnAddDestination.setOnClickListener(v -> startActivity(new Intent(homescreenActivity.this, SavedDestinationActivity.class)));

        btnAddDestination.setOnClickListener(v -> {
            if (destination != null) {

                AlertDialog.Builder addDestDlg = new AlertDialog.Builder(this);
                addDestDlg.setTitle("Save Destination");
                addDestDlg.setMessage("Insert a name for the destination");


                final EditText inName = new EditText(this);
                addDestDlg.setView(inName);

                addDestDlg.setPositiveButton("Add", (dialog, which) -> {
                    DatabaseFunctions myDb = new DatabaseFunctions(this);
                    myDb.insertRouteData("1", destination.getPosition().latitude + "," +
                            destination.getPosition().longitude, inName.getText().toString(), 0);

                });

                addDestDlg.setNegativeButton("Cancel", (dialog, which) -> {
                    AlertDialog destAlert = addDestDlg.create();
                    destAlert.dismiss();
                });

                addDestDlg.show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "No Destination Selected", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void loadDestination(String title, String inlatlng) {
        String[] latlng = inlatlng.split(",");
        LatLng destLocation = new LatLng(Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]));
        destination = new MarkerOptions().position(destLocation);
        destination.title(title);
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(destLocation));
        mMap.addMarker(destination);
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
                Toast noResultsToast = Toast.makeText(getApplicationContext(),
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
        polyLineList.add((PolylineOptions) values[0]);
    }


    public void highlightRoute(RouteDataItem selectedRouteData){

        currentRouteData = selectedRouteData;
        currentRouteLine = selectedRouteData.getPolyline();

        start_deviation_checks();

        for(int i = 0 ; i < polyLineVisibleList.size(); i++) {
            if(selectedRouteData.getPolyline().getColor() != polyLineVisibleList.get(i).getColor())
                polyLineVisibleList.get(i).remove();
        }
        mFrameLayout.setVisibility(View.GONE);

        //TODO comment these out for the alarms to work again
        //startForegroundService();
        //startTimeTriggers();
    }

    // check user deviation functions //
    public void start_deviation_checks(){
        handler.postDelayed(r, 1);
    }

    final Runnable r = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void run() {
            check_deviation();
            handler.postDelayed(this, 5000);
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void check_deviation(){
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {
                        mCurrentLocation = location;
                    }
                }
        );

        DatabaseFunctions myDb = new DatabaseFunctions(this);
        Cursor res = myDb.getUserIDOne();
        res.moveToNext();
        double tolerance = res.getInt(12); // 0.1 meters
        List<LatLng>  route = currentRouteLine.getPoints(); // Your given route
        LatLng point = new  LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        boolean exceededTolerance = false;
        System.out.println(route);
        System.out.println(point);
        if (!PolyUtil.isLocationOnPath(point, route,true, tolerance)) {
            exceededTolerance = true;
        }
        if (exceededTolerance) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            StatusBarNotification[] notifications = mNotificationManager.getActiveNotifications();
            for (StatusBarNotification notification : notifications) {
                if (notification.getId() == 2) {
                    //do nothing if notification is on the screen
                }
                else {
                    startSensorTriggerService();
                }
            }

            System.out.println("User deviated from path");
        }
        else {
            System.out.println("User HAS NOT deviated from path");
        }
    }

    // END OF check user deviation functions //

    public void listRoutes() throws ExecutionException, InterruptedException {
        int counter = 1;
        for(ArrayList<LatLng> step : stepPoints) {
            String distance = null;
            String duration = null;
            String numDuration = null;
            List<HashMap<String, String>> details = getRouteDetails(routeDetails, counter-1);
            HashMap<String, String> point = details.get(0);
            duration = point.get("duration");
            distance = point.get("distance");
            numDuration = point.get("numduration");

            CrimeCollector crimeCollector = new CrimeCollector();
            int crimeCount = crimeCollector.execute(step).get();

            routeDataList.add(new RouteDataItem(counter, crimeCount, distance, duration, numDuration, System.currentTimeMillis(), 0, polyLineList.get(counter-1)));
            counter++;
        }

        Collections.sort(routeDataList, (o1, o2) -> o1.getCrimeCount() - o2.getCrimeCount());

        if (routeDataList.size() == 3) {
            routeDataList.get(0).setImage(R.drawable.ic_route_green);
            routeDataList.get(1).setImage(R.drawable.ic_route_yellow);
            routeDataList.get(2).setImage(R.drawable.ic_route_crimson);
            routeDataList.get(0).getPolyline().color(Color.GREEN);
            routeDataList.get(1).getPolyline().color(Color.YELLOW);
            routeDataList.get(2).getPolyline().color(Color.RED);
            polyLineVisibleList.add(mMap.addPolyline(routeDataList.get(0).getPolyline()));
            polyLineVisibleList.add(mMap.addPolyline(routeDataList.get(1).getPolyline()));
            polyLineVisibleList.add(mMap.addPolyline(routeDataList.get(2).getPolyline()));
        }
        else if (routeDataList.size() == 2) {
            routeDataList.get(0).setImage(R.drawable.ic_route_green);
            routeDataList.get(1).setImage(R.drawable.ic_route_crimson);
            routeDataList.get(0).getPolyline().color(Color.GREEN);
            routeDataList.get(1).getPolyline().color(Color.RED);
            polyLineVisibleList.add(mMap.addPolyline(routeDataList.get(0).getPolyline()));
            polyLineVisibleList.add(mMap.addPolyline(routeDataList.get(1).getPolyline()));
        }
        else if (routeDataList.size() == 1) {
            routeDataList.get(0).setImage(R.drawable.ic_route_green);
            routeDataList.get(0).getPolyline().color(Color.GREEN);
            polyLineVisibleList.add(mMap.addPolyline(routeDataList.get(0).getPolyline()));
        }

        homescreenActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, RoutesFragment.newInstance(getApplicationContext(), routeDataList, homescreenActivity.this)).commit();
        mFrameLayout = findViewById(R.id.frameLayout);
        mFrameLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private static Handler disconnectHandler = new Handler(msg -> {
        return true;
    });

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startForegroundService() {
        long journeyTimeSeconds = currentRouteData.getNumTime();
        long time = journeyTimeSeconds * 1000;
        LatLng des = destination.getPosition();
        String dest = des.toString();
        String a = Double.toString(mCurrentLocation.getLatitude());
        String b = Double.toString(mCurrentLocation.getLongitude());
        String curr = "lat/lng: (" + a + "," + b + ")";

        Intent serviceIntent = new Intent(this, SensorService.class);
        serviceIntent.putExtra("dest", dest);
        serviceIntent.putExtra("curr", curr);
        serviceIntent.putExtra("timeID", time);
        startForegroundService(serviceIntent);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopForegroundService() {
        Intent serviceIntent = new Intent(this, SensorService.class);
        stopService(serviceIntent);
    }

    public void startTimeTriggers() {
        long journeyTimeSeconds = currentRouteData.getNumTime();
        long time = journeyTimeSeconds * 1000;
        LatLng des = destination.getPosition();
        String dest = des.toString();
        String a = Double.toString(mCurrentLocation.getLatitude());
        String b = Double.toString(mCurrentLocation.getLongitude());
        String curr = "lat/lng: (" + a + "," + b + ")";

        Intent serviceIntent = new Intent(this, TimeTriggerService.class);
        serviceIntent.putExtra("dest", dest);
        serviceIntent.putExtra("curr", curr);
        serviceIntent.putExtra("timeID", time);
        startService(serviceIntent);
    }

    public void stopTimeTriggersService() {
        Intent serviceIntent = new Intent(this, TimeTriggerService.class);
        stopService(serviceIntent);
    }

    public void startTimeLeftTriggerService() {
        long timeSinceStart = System.currentTimeMillis() - currentRouteData.getStartTime();
        long journeyTimeSeconds = currentRouteData.getNumTime();
        long time = journeyTimeSeconds * 1000;
        LatLng des = destination.getPosition();
        String dest = des.toString();
        String a = Double.toString(mCurrentLocation.getLatitude());
        String b = Double.toString(mCurrentLocation.getLongitude());
        String curr = "lat/lng: (" + a + "," + b + ")";

        Intent serviceIntent = new Intent(this, TimeLeftTriggerService.class);
        serviceIntent.putExtra("dest", dest);
        serviceIntent.putExtra("curr", curr);
        serviceIntent.putExtra("timeSS", timeSinceStart);
        serviceIntent.putExtra("timeID", time);
        startService(serviceIntent);
    }

    public void stopTimeLeftTriggerService() {
        Intent serviceIntent = new Intent(this, TimeLeftTriggerService.class);
        stopService(serviceIntent);
    }

    public void startNotificationsRestartService() {
        long journeyTimeSeconds = currentRouteData.getNumTime();
        long time = journeyTimeSeconds * 1000;
        LatLng des = destination.getPosition();
        String dest = des.toString();
        String a = Double.toString(mCurrentLocation.getLatitude());
        String b = Double.toString(mCurrentLocation.getLongitude());
        String curr = "lat/lng: (" + a + "," + b + ")";

        Intent serviceIntent = new Intent(this, NotificationRestartService.class);
        serviceIntent.putExtra("dest", dest);
        serviceIntent.putExtra("curr", curr);
        serviceIntent.putExtra("timeID", time);
        startService(serviceIntent);
    }

    public void stopNotificationsRestartService() {
        Intent serviceIntent = new Intent(this, NotificationRestartService.class);
        stopService(serviceIntent);
    }

    public void startSensorTriggerService() {
        long journeyTimeSeconds = currentRouteData.getNumTime();
        long time = journeyTimeSeconds * 1000;
        LatLng des = destination.getPosition();
        String dest = des.toString();
        String a = Double.toString(mCurrentLocation.getLatitude());
        String b = Double.toString(mCurrentLocation.getLongitude());
        String curr = "lat/lng: (" + a + "," + b + ")";

        Intent serviceIntent = new Intent(this, SensorTriggerService.class);
        serviceIntent.putExtra("dest", dest);
        serviceIntent.putExtra("curr", curr);
        serviceIntent.putExtra("timeID", time);
        startService(serviceIntent);
    }

    public void stopSensorTriggerService() {
        Intent serviceIntent = new Intent(this, SensorTriggerService.class);
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

        System.out.println(currentRouteData);
        if (currentRouteData != null) {
            long timeSinceStart = System.currentTimeMillis() - currentRouteData.getStartTime();
            long journeyTime = Math.round((currentRouteData.getNumTime() * 1000)*1.25) + 300000;
            long time = journeyTime - timeSinceStart;
            //Toast.makeText(getApplicationContext(), time + " " + journeyTime + " " + timeSinceStart, Toast.LENGTH_SHORT).show();
            // login if user has an active route and NotificationRestarService is running instead of TimeTriggerService
            if (NotificationRestartService.isRunning()) {
                stopNotificationsRestartService();
                startNotificationsRestartService();
                //Toast.makeText(getApplicationContext(), "Successfully logged in" + "NRS", Toast.LENGTH_SHORT).show();
            }
            // login if user has an active route and less than 3 minutes on the first timer
            else if (TimeTriggerService.isRunning() && time<180000) {
                stopTimeTriggersService();
                stopTimeLeftTriggerService();
                startNotificationsRestartService();
                //Toast.makeText(getApplicationContext(), "Successfully logged in" + "TTS", Toast.LENGTH_SHORT).show();
            }
            //login if sensor triggered the notifications
            else if (SensorTriggerService.isRunning()) {
                stopSensorTriggerService();
                startTimeLeftTriggerService();
                //Toast.makeText(getApplicationContext(), "Successfully logged in" + "STS", Toast.LENGTH_SHORT).show();
            }
            //login if sensor got triggered earlier and the timer needs to run from a specific point along the journey
            else if (TimeLeftTriggerService.isRunning()) {
                //Toast.makeText(getApplicationContext(), "Successfully logged in" + "TLTS", Toast.LENGTH_SHORT).show();
            }
            //login if user has no active route or has an active route and more than 3 minutes left on the first timer
            else {
                //Toast.makeText(getApplicationContext(), "Successfully logged in" + "inside", Toast.LENGTH_SHORT).show();
            }
        } else {
            //Toast.makeText(getApplicationContext(), "Successfully logged in" + "outside", Toast.LENGTH_SHORT).show();
        }
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
    public void onBackPressed() {
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {
            backPressedTime = t;
            Toast.makeText(getApplicationContext(), "Press Back again to Logout", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(homescreenActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
            finish();
        }
    }



}

