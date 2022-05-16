package com.inhatc.herewhere;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Array;

public class JoinActivity extends AppCompatActivity {

    private static final String TAG = "JoinActivity";
    private FirebaseAuth firebaseAuth;

    private EditText inputID;
    private EditText inputPW;
    private EditText inputPW2;
    private EditText inputPhone;
    private EditText inputPhone2;
    private EditText inputName;
    private EditText inputBirth;
    private EditText inputHeight;
    private EditText inputWeight;

    private String ID;
    private String PW;
    private String PW2;
    private String phone;
    private String phone2;
    private String name;
    private String birth;
    private String height;
    private String weight;
    private String gender;

    private RadioGroup radioGroup;
    private Spinner spinner;
    ArrayAdapter<CharSequence> adapter = null;
    String bloodType;

    private Button btnCheck;
    private Button btnJoin;


    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        inputID = findViewById(R.id.inputID);
        inputPW = findViewById(R.id.inputPW);
        inputPW2 = findViewById(R.id.inputPW2);
        inputPhone = findViewById(R.id.inputPhone);
        inputPhone2 = findViewById(R.id.inputPhone2);
        inputName = findViewById(R.id.inputName);
        inputBirth = findViewById(R.id.inputBirth);
        inputHeight = findViewById(R.id.inputHeight);
        inputWeight = findViewById(R.id.inputWeight);

        spinner = findViewById(R.id.spinner);
        radioGroup = findViewById(R.id.Radiogroup);

        btnCheck = (Button) findViewById(R.id.btnCheck);
        btnJoin = (Button) findViewById(R.id.btnJoin);

        // 혈액형
        adapter = ArrayAdapter.createFromResource(this, R.array.blood, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bloodType = (String)spinner.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 성별 체크
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkGender) {
                switch(checkGender) {
                    case R.id.btnMan:
                        gender = "남자";
                        break;
                    case R.id.btnWoman:
                        gender = "여자";
                }
            }
        });

        // 회원가입 버튼
        btnJoin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                userInfoToString();

                if (!ID.equals("") && !PW.equals("") && !name.equals("") && !phone.equals("") && !phone2.equals("") &&
                    !birth.equals("") && !height.equals("") && !weight.equals("") && !bloodType.equals("") && !gender.equals("")) {
                    if (pwCheck() == 0) {
                        Toast.makeText(JoinActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                        Log.v(TAG, "PW: " + PW + ", PW2: " + PW2);
                    } else {
                        createUser(ID, PW, phone, phone2, name, birth, height, weight, bloodType, gender);
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    public void userInfoToString() {
        ID = inputID.getText().toString();
        PW = inputPW.getText().toString();
        PW2 = inputPW2.getText().toString();
        phone = inputPhone.getText().toString();
        phone2 = inputPhone2.getText().toString();
        name = inputName.getText().toString();
        birth = inputBirth.getText().toString();
        height = inputHeight.getText().toString();
        weight = inputWeight.getText().toString();
    }

    public int pwCheck() {
        int pwCheck;
        if (PW2.equals(PW)) {
            pwCheck = 1;
        } else
            pwCheck = 0;

        return pwCheck;
    }

    // 회원가입
    private void createUser(String ID, String PW, String phone, String phone2, String name, String birth,
                            String height, String weight, String bloodType, String gender) {
        User user = new User(ID, PW, phone, phone2, name, birth, height, weight, bloodType, gender);

        databaseReference.child("users").child(ID).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(JoinActivity.this, "회원가입을 완료하였습니다.", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(JoinActivity.this, "회원가입을 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
