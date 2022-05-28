package com.inhatc.herewhere;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    private TextView txtID;
    private SwitchButton switchMessage;

    private static final String TAG = "MessageActivity:: ";

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        txtID = findViewById(R.id.txtID);
        Intent myIntent = getIntent(); /*데이터 수신*/

        String id = myIntent.getExtras().getString("id"); /*String형*/
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
                    databaseReference.child("users").updateChildren(activateGuardianMessage);
                    Toast.makeText(MessageActivity.this, "일정시간마다 보호자에게 메세지를 전송합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // 스위치 버튼 비활성화
                    databaseReference.child("users").updateChildren(deactivateGuardianMessage);
                    Toast.makeText(MessageActivity.this, "보호자에게 메세지를 전송하지 않습니다.", Toast.LENGTH_SHORT).show();
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
}
