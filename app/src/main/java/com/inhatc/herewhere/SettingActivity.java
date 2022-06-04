package com.inhatc.herewhere;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingActivity extends AppCompatActivity {

    private static final String TAG = "SettingActivity";

    private TextView txtID;
    private Button btnTag, btnMotion, btnMessage, btnSOS;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    private int locationRequestCode = 1000;

    private LocationManager locationManager;
    private LocationListener locationListener;
    Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        txtID = findViewById(R.id.txtID);
        Intent myIntent = getIntent(); /*데이터 수신*/

        String id = myIntent.getExtras().getString("id"); /*String형*/
        txtID.setText(id+"님");

        txtID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), MyinfoActivity.class);
                // 로그인 정보 같이 보내기
                myIntent.putExtra("id", id);
                startActivity(myIntent);
            }
        });

        btnTag = findViewById(R.id.btnTag);
        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), NFCtagActivity.class);
                // 로그인 정보 같이 보내기
                myIntent.putExtra("id", id);
                startActivity(myIntent);
            }
        });

        btnMotion = findViewById(R.id.btnMotion);
        btnMotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), MotionActivity.class);
                // 로그인 정보 같이 보내기
                myIntent.putExtra("id", id);
                startActivity(myIntent);
            }
        });

        btnMessage = findViewById(R.id.btnMessage);
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), MessageActivity.class);
                // 로그인 정보 같이 보내기
                myIntent.putExtra("id", id);
                startActivity(myIntent);
            }
        });

        btnSOS = findViewById(R.id.btnSOS);
        btnSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingGPS();
                Location userLocation = getMyLocation();
                if( userLocation != null ) {
                    double latitude = userLocation.getLatitude();
                    double longitude = userLocation.getLongitude();
                    Log.d(TAG, "onClick :: longitude=" + longitude + ", latitude=" + latitude);
                    sendSosMessage(latitude, longitude, id);
                }
            }
        });

    }

    public Location getMyLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "request for permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, locationRequestCode);
            getMyLocation();
        } else {
            Log.d(TAG, "already permission granted");
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

                    String sms = name + "님의 구조 요청. ( 사용자 정보 : 보호자 전화번호 " + guardianPhone + " / 생년월일 " +
                            birth + " / 키 " + height + " / 몸무게 " + weight + " / 성별 " + gender + " / 혈액형 " + bloodType + " )" +
                            "( 사용자 위치 정보 : 위도 " + latitude + " / 경도 " + longitude + " )";
                    Log.d(TAG, sms);

                    //String sosNo = "119";

                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        // smsManager.sendTextMessage(guardianPhone, null, sms, null, null);
                        // smsManager.sendTextMessage(sosNo, null, sms, null, null);
                        Toast.makeText(getApplicationContext(), "구조 요청 완료", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "구조 요청 실패", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}