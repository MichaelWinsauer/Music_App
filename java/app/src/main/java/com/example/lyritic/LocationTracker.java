package com.example.lyritic;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Looper;
import android.telephony.TelephonyManager;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class LocationTracker extends AsyncTask<Location, Void, String> {
    Double lat;
    Double lng;
    Context context;
    String phoneNumber;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    public LocationTracker(Context context) {
        this.context = context;

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            phoneNumber = tm.getLine1Number();
        }

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initialize();
        }
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

                    LocationData locationData = new LocationData(lat, lng);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();

                    if(phoneNumber.equals("")) {
                        phoneNumber = "unknown Number";
                    }

                    dbRef.child(phoneNumber).child(formatter.format(now)).setValue(locationData);
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
