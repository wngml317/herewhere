package com.inhatc.herewhere;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class MotionSensorService extends Service implements SensorEventListener {

    private static final String TAG = "MotionSensorService:: ";
    private String channelID = "channelID";

    private SensorManager sensorManager = null;
    private Sensor gravitySensor = null;

    private double gravityY;

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

                while(true) {
                    // 실행시킬 함수
                    motionSensor();

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

    private void motionSensor() {
        Log.w(TAG, "gravityY: " + String.format("%.4f", gravityY));
    }


}