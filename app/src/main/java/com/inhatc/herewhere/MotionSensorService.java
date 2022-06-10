package com.inhatc.herewhere;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.inhatc.herewhere.SettingActivity;

public class MotionSensorService extends Service implements SensorEventListener {

    private static final String TAG = "MotionSensorService:: ";
    private String channelID = "channelID";

    private SensorManager sensorManager = null;
    private Sensor gravitySensor = null;

    private LocationManager locationManager;
    private LocationListener locationListener;
    Location currentLocation;
    private int locationRequestCode = 1000;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    private double gravityY;
    private int count = 0;
    boolean sw = true;

    public MotionSensorService() {
    }

    @Override
    public void onCreate(){
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);

        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences autoId = getSharedPreferences("id", MODE_PRIVATE);
        String id = autoId.getString("id", "");

        String motionValue = intent.getExtras().getString("motionValue");
        if (motionValue.equals("no")) {
            stopSelf();
        }

        Log.v(TAG, "onStartCommend");

        if(intent == null){
            return START_STICKY;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel =
                    new NotificationChannel(channelID, "여기어디", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(serviceChannel);
        }

        Intent notificationIntent = new Intent(this, SettingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, channelID)
                .setContentTitle("여기어디")
                .setContentText("움직임 감지중🚶‍♂️")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();


        startForeground(1, notification);

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(sw) {
                    // 실행시킬 함수
                    Log.w(TAG, "gravityY: " + String.format("%.4f", gravityY));

                    if (gravityY < 0) {
                        Log.v(TAG, "움직임에 이상 감지 " + count + "초 경과");
                        count += 10;

                        if ((count % 50) == 0) {
                            Log.v(TAG, "움직임에 이상이 감지되어 신고를 진행합니다.");
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
                    } else {
                        count = 0;
                    }

                    try {
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
        super.onDestroy();
        sw = false;
        Log.v(TAG, "onDestroy()");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == gravitySensor) {
            gravityY = event.values[1];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // 문자 전송 권한 확인
    private String smsPermissionCheck(){
        int smsPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        String state;
        if(smsPermissionCheck == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "ACCESS_BACKGROUND_SMS::allow");
            state = "allow";
        }else{
            Toast.makeText(this, "SMS 권한 설정을 허용으로 변경하세요.", Toast.LENGTH_SHORT).show();
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

    public Location getMyLocation(){

        // 위치 권한 확인
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "ACCESS_BACKGROUND_LOCATION::deny");
            getMyLocation();
        } else {
            Log.d(TAG, "ACCESS_BACKGROUND_LOCATION::allow");

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

                    String sms = name + "님의 구조 요청. ( 사용자 정보 : 보호자 전화번호 " + guardianPhone + " / 생년월일 " +
                            birth + " / 키 " + height + " / 몸무게 " + weight + " / 성별 " + gender + " / 혈액형 " + bloodType + " )" +
                            "( 사용자 위치 정보 : 위도 " + latitude + " / 경도 " + longitude + " )";
                    Log.d(TAG, sms);

                    String testPhone = "전화번호 입력";
                    String testSMS = "구조요청/홍주희/여/20001017/180cm/79kg/RH+ O/보호자:010-1234-1234/위도:37.421/경도:-122.084";
                    Log.d(TAG, "testSMS :: " + testSMS);

                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(testPhone, null, testSMS, null, null);
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