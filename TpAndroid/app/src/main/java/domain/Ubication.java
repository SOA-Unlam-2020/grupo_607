package domain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.security.Provider;

public class Ubication implements LocationListener {
    private Context ctx;
    LocationManager locationManager;
    String provider;
    private boolean networkOn;

    @SuppressLint("MissingPermission")
    public Ubication(Context ctx){
        this.ctx = ctx;
        locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        provider = LocationManager.NETWORK_PROVIDER;
        networkOn = locationManager.isProviderEnabled(provider);
        locationManager.requestLocationUpdates(provider,1000,1,this);
    }

    public double getAltitude(){

    @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(provider);
    return location.getAltitude();
    }

    public double getLatitude(){

        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(provider);
        return location.getLatitude();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
