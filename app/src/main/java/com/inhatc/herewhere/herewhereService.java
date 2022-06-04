package com.inhatc.herewhere;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.Timer;
import java.util.TimerTask;

public class herewhereService extends Service {

    private static final String TAG = "herewhereService";
    boolean bool = true;

    private LocationManager locationManager;
    private LocationListener locationListener;
    Location currentLocation;

    public herewhereService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "service :: onStartCommand");

        if(intent == null){
            return Service.START_STICKY;
        }

        settingGPS();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(bool) {
                    try {
                        Thread.sleep(10000);
                        Log.d(TAG, "service :: 10초 경과");

                        Handler mHandler = new Handler(Looper.getMainLooper());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Location userLocation = getMyLocation();
                                if( userLocation != null ) {
                                    double latitude = userLocation.getLatitude();
                                    double longitude = userLocation.getLongitude();
                                    Log.d(TAG, "service :: longitude=" + longitude + ", latitude=" + latitude);
                                }
                            }
                        }, 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "service :: onDestory");
        bool = false;
    }

    public Location getMyLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "service :: request for permission");
        } else {
            Log.d(TAG, "service :: already permission granted");
            // 10초 간격으로 업데이트
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            if (currentLocation != null) {
                double lng = currentLocation.getLongitude();
                double lat = currentLocation.getLatitude();
            }else{
                getMyLocation();
            }
        }
        return currentLocation;
    }

    private void settingGPS() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {        }
            public void onProviderEnabled(String provider) {        }
            public void onProviderDisabled(String provider) {        }
        };
    }
}