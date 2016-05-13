package com.synaptop.mobile.myapplication.map;

import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.synaptop.mobile.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BixBike extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, OnMyLocationButtonClickListener {
    public static final String LOG_TAG = BixBike.class.getSimpleName();

    private GoogleMap mMap;

    private LocationManager myLocationManager; // to get current location


    private boolean dataIsNull = false; //flag to check if Json data is empty

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bix_bike);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        FetchPlacesTask placesTask = new FetchPlacesTask();
        placesTask.execute();
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
        getCurrentPosition();

    }
    //move camera to current location
    private void getCurrentPosition() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            Location l = getLastKnownLocation();
            double la = l.getLatitude();
            double lng = l.getLongitude();
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(la, lng))
                    .zoom(14).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        }
    }

    //get the current Location value
    private Location getLastKnownLocation() {
        myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = myLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
                // Access to the location has been granted to the app.
                Location l = myLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
        }
        return bestLocation;
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.

            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            getCurrentPosition();
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    /***
     * Transmit JSON from URL into Arraylist
     */
    public class FetchPlacesTask extends AsyncTask<String, Void, ArrayList<com.synaptop.mobile.myapplication.data.Places>> {
        private final String LOG_TAG = FetchPlacesTask.class.getSimpleName();


        protected ArrayList<com.synaptop.mobile.myapplication.data.Places> doInBackground(String... params) {

            final String BASE_URL = "http://feeds.bikesharetoronto.com/stations/stations.json";

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String placesJsonStr = null;
            try {
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .build();
                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI " + url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                placesJsonStr = buffer.toString();
                Log.v(LOG_TAG, " JSON String" + placesJsonStr);
            } catch (IOException e) {
                Log.e("LOG_TAG", "Error " + e);
                return null;

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("LOG_TAG", "Error closing stream", e);
                    }
                }
            }
            // return data to onPostExecute
            try {
                return getPlacesDataFromJson(placesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        private ArrayList<com.synaptop.mobile.myapplication.data.Places> getPlacesDataFromJson(String placeJsonStr)
                throws JSONException {
            ArrayList<com.synaptop.mobile.myapplication.data.Places> places;

            // These are the names of the JSON objects that need to be extracted.
            final String PLACES_ITEMS = "stationBeanList";
            JSONArray placesArray = new JSONObject(placeJsonStr).getJSONArray(PLACES_ITEMS);
            Log.v(LOG_TAG, "placesArray_JSON String " + placesArray.toString());
            int numberOfPlaces = placesArray.length();
            if (numberOfPlaces == 0) {
                dataIsNull = true;
            }

            places = new ArrayList<com.synaptop.mobile.myapplication.data.Places>();
            for (int i = 0; i < numberOfPlaces; i++) {
                final String PLACES_ID = "id";
                final String PLACES_LATITUDE = "latitude";
                final String PLACES_LONGITUDE = "longitude";
                final String PLACES_NAME = "stationName";
                final String PLACES_AVAILABLE = "availableBikes";
                // Get the JSON object representing a bike station
                JSONObject placeObject = placesArray.getJSONObject(i);
                String id = placeObject.getString(PLACES_ID);
                float lat = Float.parseFloat(placeObject.getString(PLACES_LATITUDE));
                float lng = Float.parseFloat(placeObject.getString(PLACES_LONGITUDE));
                String title = placeObject.getString(PLACES_NAME);
                String available = placeObject.getString(PLACES_AVAILABLE);

                places.add(new com.synaptop.mobile.myapplication.data.Places(lng, lat, id,title ,available));
            }
            return places;
        }
        //shoot spots on the screen after the JSON fully loaded
        protected void onPostExecute(ArrayList<com.synaptop.mobile.myapplication.data.Places> result) {
            if (result != null) {
                //display all spots on the map
                for (com.synaptop.mobile.myapplication.data.Places i : result) {
                    LatLng spot = new LatLng(i.lat, i.lng);
                    mMap.addMarker(new MarkerOptions().position(spot).title(i.title+ "  Bike available: " + i.available ));
                }
            }
        }
    }


}
