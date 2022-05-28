package com.inhatc.herewhere;

import android.content.Intent;
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
                    // 로그인 정보 같이 보내기
                    myIntent.putExtra("id", ID);
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
}
