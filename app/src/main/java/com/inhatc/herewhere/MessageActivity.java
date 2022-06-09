package com.inhatc.herewhere;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.security.identity.CipherSuiteNotSupportedException;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    private TextView txtID;
    private SwitchButton switchMessage;

    private static final String TAG = "MessageActivity:: ";

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        txtID = findViewById(R.id.txtID);
//        Intent myIntent = getIntent(); /*데이터 수신*/
//        String id = myIntent.getExtras().getString("id"); /*String형*/

        SharedPreferences autoId = getSharedPreferences("id", MODE_PRIVATE);
        id = autoId.getString("id", "");
        txtID.setText(id+"님");

        switchMessage = findViewById(R.id.switchMessage);

        // 보호자 메세지 전송 활성여부 확인 후 스위치버튼 체크
        checkGuardianMessage(id);

        switchMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Map<String, Object> activateGuardianMessage = new HashMap<String, Object>();
                Map<String, Object> deactivateGuardianMessage = new HashMap<String, Object>();
                activateGuardianMessage.put(id + "/" + "guardianMessage", "yes");
                deactivateGuardianMessage.put(id + "/" + "guardianMessage", "no");

                // 스위치 버튼 활성화
                if (isChecked) {
                    String state = permissionCheck();
                    if(state.equals("allow")){
                        databaseReference.child("users").updateChildren(activateGuardianMessage);
                        Toast.makeText(MessageActivity.this, "문자 전송 기능 활성화", Toast.LENGTH_SHORT).show();
                        startMessageService("yes");
                    }else{
                        finish();
                    }
                } else {
                    // 스위치 버튼 비활성화
                    databaseReference.child("users").updateChildren(deactivateGuardianMessage);
                    Toast.makeText(MessageActivity.this, "문자 전송 기능 비활성화", Toast.LENGTH_SHORT).show();
                    stopMessageService("no");
                }
            }
        });
    }

    // 보호자 메세지 전송 활성여부 확인
    private void checkGuardianMessage(String id) {

        // 로그인 한 회원의 motionSensor값 확인
        databaseReference.child("users").child(id).child("guardianMessage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String GuardianMessage_val;
                GuardianMessage_val = (String) snapshot.getValue();

                if (GuardianMessage_val.equals("yes")) {
                    Log.v(TAG, "GuardianMessage_val: " + GuardianMessage_val);
                    switchMessage.setChecked(true);
                } else {
                    Log.v(TAG, "GuardianMessage_val: " + GuardianMessage_val);
                    switchMessage.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "checkGuardianMessage_onCancelled: " + error.toException());
            }
        });

    }

    private String permissionCheck(){
        int gpsPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        int smsPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        String state;
        if(gpsPermissionCheck == PackageManager.PERMISSION_GRANTED && smsPermissionCheck == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "ACCESS_BACKGROUND_LOCATION::allow");
            state = "allow";
        }else{
            Toast.makeText(MessageActivity.this, "GPS 및 SMS 권한 설정을 항상 허용으로 변경하세요.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "ACCESS_BACKGROUND_LOCATION::deny");
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
            state = "deny";
        }
        return state;
    }

    // MessageService 실행
    public void startMessageService(String motionSensor_val) {
        Intent serviceIntent = new Intent(this, MessageService.class);
        serviceIntent.putExtra("id", id);
        startService(serviceIntent);
    }

    // MessageService 중지
    public void stopMessageService(String motionSensor_val) {
        Intent serviceIntent = new Intent(this, MessageService.class);
        stopService(serviceIntent);
    }
}
