package com.inhatc.herewhere;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingActivity extends AppCompatActivity {

    private static final String TAG = "SettingActivity::";

    private TextView txtID;
    private Button btnTag, btnMotion, btnMessage, btnSOS;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    private int locationRequestCode = 1000;
    private int SMS_SEND_PERMISSION = 1;

    private LocationManager locationManager;
    private LocationListener locationListener;
    Location currentLocation;

    private long backKeyPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        SharedPreferences autoId = getSharedPreferences("id", MODE_PRIVATE);
        String id = autoId.getString("id", "");
        txtID = findViewById(R.id.txtID);
//        Intent myIntent = getIntent(); /*????????? ??????*/
//        String id = myIntent.getExtras().getString("id"); /*String???*/
        txtID.setText(id+"???");

        txtID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), MyinfoActivity.class);
                // ????????? ?????? ?????? ?????????
                myIntent.putExtra("id", id);
                startActivity(myIntent);
            }
        });

        btnTag = findViewById(R.id.btnTag);
        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), NFCtagActivity.class);
                // ????????? ?????? ?????? ?????????
                myIntent.putExtra("id", id);
                startActivity(myIntent);
            }
        });

        btnMotion = findViewById(R.id.btnMotion);
        btnMotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), MotionActivity.class);
                // ????????? ?????? ?????? ?????????
                myIntent.putExtra("id", id);
                startActivity(myIntent);
            }
        });

        btnMessage = findViewById(R.id.btnMessage);
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), MessageActivity.class);
                // ????????? ?????? ?????? ?????????
                myIntent.putExtra("id", id);
                startActivity(myIntent);
            }
        });

        btnSOS = findViewById(R.id.btnSOS);
        btnSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String state = smsPermissionCheck();
                if(state.equals("allow")){
                    settingGPS();
                    Location userLocation = getMyLocation();
                    if( userLocation != null ) {
                        double latitude = userLocation.getLatitude();
                        double longitude = userLocation.getLongitude();
                        Log.d(TAG, "onClick :: longitude=" + longitude + ", latitude=" + latitude);
                        sendSosMessage(latitude, longitude, id);
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // ?????? ?????? ?????? ????????? ????????? ?????? ?????? ?????? ?????? ?????? ??????

        // ??????????????? ?????? ?????? ????????? ????????? ????????? 2.5?????? ?????? ?????? ????????? ?????? ???
        // ??????????????? ?????? ?????? ????????? ????????? ????????? 2.5?????? ???????????? Toast ??????
        // 2500 milliseconds = 2.5 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "?????? ?????? ????????? ??? ??? ??? ????????? ???????????????.", Toast.LENGTH_LONG).show();
            return;
        }
        // ??????????????? ?????? ?????? ????????? ????????? ????????? 2.5?????? ?????? ?????? ????????? ?????? ???
        // ??????????????? ?????? ?????? ????????? ????????? ????????? 2.5?????? ????????? ???????????? ??????
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();
            Toast.makeText(this,"????????? ????????? ???????????????.",Toast.LENGTH_LONG).show();
        }
    }

    private String smsPermissionCheck(){
        int smsPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        String state;
        if(smsPermissionCheck == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "ACCESS_BACKGROUND_SMS::allow");
            state = "allow";
        }else{
            Toast.makeText(SettingActivity.this, "SMS ?????? ????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "ACCESS_BACKGROUND_SMS::deny");
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_SEND_PERMISSION);
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
            state = "deny";
        }
        return state;
    }

    public Location getMyLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "ACCESS_BACKGROUND_LOCATION::deny");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, locationRequestCode);
            getMyLocation();
        } else {
            Log.d(TAG, "ACCESS_BACKGROUND_LOCATION::allow");
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
                //Log.d(TAG, "settingGPS :: longitude=" + longitude + ", latitude=" + latitude);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {        }
            public void onProviderEnabled(String provider) {        }
            public void onProviderDisabled(String provider) {        }
        };
    }

    public void sendSosMessage(double latitude, double longitude, String id){
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
                    String birth = user.getBirth();
                    String height = user.getHeight();
                    String weight = user.getWeight();
                    String gender = user.getGender();
                    String bloodType = user.getBloodType();
                    //String sosNo = "119";

                    String sms = name + "?????? ?????? ??????. ( ????????? ?????? : ????????? ???????????? " + guardianPhone + " / ???????????? " +
                            birth + " / ??? " + height + " / ????????? " + weight + " / ?????? " + gender + " / ????????? " + bloodType + " )" +
                            "( ????????? ?????? ?????? : ?????? " + latitude + " / ?????? " + longitude + " )";
                    Log.d(TAG, sms);

                    String testPhone = "01023625681";
                    String testSMS = "????????????/?????????/???/20001017/180cm/79kg/RH+ O/?????????:010-1234-1234/??????:37.421/??????:-122.084";
                    Log.d(TAG, "testSMS :: " + testSMS);

                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(testPhone, null, testSMS, null, null);
                        // smsManager.sendTextMessage(guardianPhone, null, sms, null, null);
                        // smsManager.sendTextMessage(sosNo, null, sms, null, null);
                        Toast.makeText(getApplicationContext(), "?????? ?????? ??????", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "?????? ?????? ??????", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}