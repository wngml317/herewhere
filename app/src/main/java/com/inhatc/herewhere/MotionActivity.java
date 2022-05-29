package com.inhatc.herewhere;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.HashMap;
import java.util.Map;

public class MotionActivity extends AppCompatActivity {

    private TextView txtID;
    private SwitchButton switchMotion;

    private static final String TAG = "MotionActivity:: ";

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);

        txtID = findViewById(R.id.txtID);
        Intent myIntent = getIntent(); /*데이터 수신*/

        String id = myIntent.getExtras().getString("id"); /*String형*/
        txtID.setText(id+"님");

        // 움직임 감지 활성여부 확인 후 스위치버튼 체크
        checkMotionSensor(id);

        switchMotion = findViewById(R.id.switchMotion);
        switchMotion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Map<String, Object> activateMotionSensor = new HashMap<String, Object>();
                Map<String, Object> deactivateMotionSensor = new HashMap<String, Object>();
                activateMotionSensor.put(id + "/" + "motionSensor", "yes");
                deactivateMotionSensor.put(id + "/" + "motionSensor", "no");

                // 스위치 버튼 활성화
                if (isChecked) {
                    databaseReference.child("users").updateChildren(activateMotionSensor);
                    Toast.makeText(MotionActivity.this, "움직임 감지가 활성화 되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // 스위치 버튼 비활성화
                    databaseReference.child("users").updateChildren(deactivateMotionSensor);
                    Toast.makeText(MotionActivity.this, "움직임 감지가 비활성화 되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 움직임 감지 활성여부 확인
    private void checkMotionSensor(String id) {

        // 로그인 한 회원의 motionSensor값 확인
        databaseReference.child("users").child(id).child("motionSensor").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String motionSensor_val;
                motionSensor_val = (String) snapshot.getValue();

                if (motionSensor_val.equals("yes")) {
                    Log.v(TAG, "motionSensor_val: " + motionSensor_val);
                    switchMotion.setChecked(true);
                } else {
                    Log.v(TAG, "motionSensor_val: " + motionSensor_val);
                    switchMotion.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("MotionActivity:::", "checkMotionSensor_onCancelled: " + error.toException());
            }
        });

    }
}
