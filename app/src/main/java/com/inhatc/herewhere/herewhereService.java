package com.inhatc.herewhere;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class herewhereService extends Service {

    private static final String TAG = "herewhereService";

    public herewhereService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand 호출");
        test();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void test(){
        for(int i = 1; i < 5; i++){
            try{
                Thread.sleep(1000);
            }catch (Exception e){}
            Log.d(TAG, "count :: "+ i);
        }
    }
}