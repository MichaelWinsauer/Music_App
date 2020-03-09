package com.example.lyritic;

import java.io.Serializable;

public class LocationData implements Serializable {

    private double latitude;
    private double longitude;

    public LocationData() {

    }

    public LocationData(double lat, double lng) {
        this.latitude = lat;
        this.longitude = lng;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
