package com.example.lyritic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.telephony.TelephonyManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class LocationTracker extends AsyncTask<Location, Void, String>{
    Double lat;
    Double lng;
    Context context;


    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

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

                    LocationData locationData = new LocationData(lat, lng);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();

                    TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                    @SuppressLint("MissingPermission") String phoneNumber = tMgr.getLine1Number();


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
