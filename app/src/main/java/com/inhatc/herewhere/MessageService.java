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

public class MessageService extends Service {

    private static final String TAG = "MessageService";
    boolean bool = true;

    private LocationManager locationManager;
    private LocationListener locationListener;
    Location currentLocation;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    public MessageService() {
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
                        // 시간 간격 : 10초
                        Thread.sleep(10000);
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

                    String sms =  "보호자에게 " + name + "님의 위치 정보를 전송합니다. " + "( 위도 " + latitude + " / 경도 " + longitude + " )";
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