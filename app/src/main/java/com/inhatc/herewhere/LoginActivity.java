package com.inhatc.herewhere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    private String ID;
    private String PW;

    Button btnLogin, btnJoin;
    EditText inputID, inputPW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputID = findViewById(R.id.inputID);
        inputPW = findViewById(R.id.inputPW);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login(inputID.getText().toString());
            }
        });

        btnJoin = findViewById(R.id.btnJoin);
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class );
                startActivity(intent);
            }
        });

    }

    private void Login(String id) {

        // 입력한 아이디로 가입된 user가 있는지 확인
        databaseReference.child("users").child(id).child("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String id_val;
                id_val = (String) snapshot.getValue();

                if (id_val == null) {
                    Toast.makeText(LoginActivity.this, "아이디가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();

                } else {
                    Login_pw();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("LoginActivity:::", "onCancelled_idDuplicateCheck: " + error.toException());
            }
        });

    }

    // 비밀번호 확인
    private void Login_pw() {

        ID = inputID.getText().toString();
        PW = inputPW.getText().toString();
        // 입력한 아이디가 존재할 때 비밀번호가 맞는지 확인
        databaseReference.child("users").child(ID).child("pw").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String pw_val;
                pw_val = (String) snapshot.getValue();

                if (PW.equals(pw_val)) {
                    Intent myIntent = new Intent(getApplicationContext(), SettingActivity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    SharedPreferences autoId = getSharedPreferences("id", MODE_PRIVATE);
                    SharedPreferences.Editor autoIdEditor = autoId.edit();
                    autoIdEditor.putString("id", ID);
                    autoIdEditor.commit();

                    Log.v(TAG, "autoId: " + autoId.getString("id", ""));
                    // 로그인 정보 같이 보내기
//                    myIntent.putExtra("id", ID);
                    // checkMotionSensor(ID);

                    startActivity(myIntent);
                } else {
                    Log.v("LoginActivity:::", "onDataChange_getIdValue: " + pw_val);
                    Toast.makeText(LoginActivity.this, "비밀번호가 일치하지 않습니다..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("LoginActivity:::", "onCancelled_idDuplicateCheck: " + error.toException());
            }
        });

    }

    // 움직임감지 활성여부 확인
    public void checkMotionSensor(String id) {

        // 로그인 한 회원의 motionSensor값 확인
        databaseReference.child("users").child(id).child("motionSensor").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String motionSensor_val;
                motionSensor_val = (String) snapshot.getValue();

                Log.v(TAG, "checkMotionSensor(): " + motionSensor_val);

                if (motionSensor_val.equals("yes")) {
                    motionSensor();
                } else {
                    Log.v(TAG, "checkMotionSensor()_motionSensor_val: no");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "checkMotionSensor_onCancelled: " + error.toException());
            }
        });
    }

    // 움직임감지 실행
    public void motionSensor() {

        Log.v(TAG, "motionSensor(): yes");
        Log.v(TAG, "motionSensor: " + "움직임 감지 실행");
        Intent serviceIntent = new Intent(this, MotionSensorService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
            Log.v(TAG, "Build.VERSION.SDK_INT >= Build.VERSION_CODES.0: " + "움직임 감지 실행");
        } else startService(serviceIntent);
    }

}
