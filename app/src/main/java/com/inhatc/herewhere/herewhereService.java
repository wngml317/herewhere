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
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class herewhereService extends Service {

    private static final String TAG = "herewhereService";
    boolean bool = true;

    private LocationManager locationManager;
    private LocationListener locationListener;
    Location currentLocation;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

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
        String id = intent.getExtras().getString("id");

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
                        Log.d(TAG, "service :: 10??? ??????");

                        Handler mHandler = new Handler(Looper.getMainLooper());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Location userLocation = getMyLocation();
                                if( userLocation != null ) {
                                    double latitude = userLocation.getLatitude();
                                    double longitude = userLocation.getLongitude();
                                    Log.d(TAG, "service :: longitude=" + longitude + ", latitude=" + latitude);
                                    sendMessage(longitude, latitude, id);
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
            // 10??? ???????????? ????????????
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

    public void sendMessage(double latitude, double longitude, String id){
        databaseReference.child("users").child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "Error getting data", task.getException());
                }
                else {
                    Log.d(TAG, String.valueOf(task.getResult().getValue()));
                    User user = task.getResult().getValue(User.class);
                    String guardianPhone = user.getPhone2();
                    String name = user.getName();

                    String sms =  "??????????????? " + name + "?????? ?????? ????????? ???????????????. " + "( ?????? " + latitude + " / ?????? " + longitude + " )";
                    Log.d(TAG, sms);

                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        // smsManager.sendTextMessage(guardianPhone, null, sms, null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}