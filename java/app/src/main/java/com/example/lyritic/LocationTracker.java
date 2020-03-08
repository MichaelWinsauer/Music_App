package com.example.lyritic;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public class LocationTracker extends AsyncTask<Location, Void, String>{
    Double lat;
    Double lng;
    Context context;


    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;


    public LocationTracker(Context context) {
        this.context = context;
        initialize();
    }

    private void initialize() {


        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {

                    lat = location.getLatitude();
                    lng = location.getLongitude();

                    //new LocationTracker(context).execute(location);

//                    Toast.makeText(context, location.getLatitude() + "  :  " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                }
                super.onLocationResult(locationResult);
            }
        };

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

    }


    public String sendData(double lat, double lng) {

        return null;
    }


    @Override
    protected String doInBackground(Location... locations) {
        return sendData(locations[0].getLatitude(), locations[0].getLongitude());
    }
}
