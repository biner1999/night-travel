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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class homescreenActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

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
    private MarkerOptions destination;
    private Polyline currentPolyline;
    Button getDirection;



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
        mSearchText = findViewById(R.id.input_search);
        MapsInitializer.initialize(getApplicationContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getDirection = findViewById(R.id.btnGetDirection);
        getDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FetchURL(homescreenActivity.this).execute(getUrl(mCurrentLocation, destination.getPosition(), "walking"), "walking");
            }
        });

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

    private String getUrl(Location origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.getLatitude() + "," + origin.getLongitude(); //String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
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

        destMarker = mMap.addMarker( new MarkerOptions()
                .position(addressLtLn).title(address.getAddressLine(0))
                .draggable(true));

        addresses.clear();

        destination = new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())).title("Location 2");

        return destination;

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
    }

    private void calculateDirections(Marker marker) {
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        mCurrentLocation.getLatitude(),
                        mCurrentLocation.getLongitude()
                )
        );

        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG,"calculateDirections : routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections : duration " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections : distance " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections : geoCodedWayPoints " + result.geocodedWaypoints[0].toString());
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "onFailure: " + e.getMessage() );
            }
        });
    }

    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
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

