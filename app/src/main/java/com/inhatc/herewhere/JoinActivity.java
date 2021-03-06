package com.inhatc.herewhere;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kyleduo.switchbutton.SwitchButton;

import java.sql.Array;
import java.util.HashMap;
import java.util.Map;

public class JoinActivity extends AppCompatActivity {

    private static final String TAG = "JoinActivity:: ";
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
    private String motionSensor;
    private String guardianMessage;

    private String idCheck = "";

    private RadioGroup radioGroup;
    private Spinner spinner;
    private String bloodType;

    private SwitchButton switchMotion;
    private SwitchButton switchMessage;

    private Button btnCheck;
    private Button btnJoin;


    final FirebaseDatabase database = FirebaseDatabase.getInstance();
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

        switchMotion = findViewById(R.id.switchMotion);
        switchMessage = findViewById(R.id.switchMessage);
        motionSensor = "no";
        guardianMessage = "no";

        btnCheck = findViewById(R.id.btnCheck);
        btnJoin = findViewById(R.id.btnJoin);

        // ?????????
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.blood, android.R.layout.simple_spinner_dropdown_item);
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

        // ?????? ??????
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkGender) {
                switch(checkGender) {
                    case R.id.btnMan:
                        gender = "??????";
                        break;
                    case R.id.btnWoman:
                        gender = "??????";
                }
            }
        });

        // ????????? ???????????? ??????
        btnCheck.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (inputID.getText().toString().equals("")) {
                    Toast.makeText(JoinActivity.this, "???????????? ??????????????????.", Toast.LENGTH_LONG).show();
                } else {
                    idDuplicateCheck(inputID.getText().toString());
                }
            }
        });

        // ???????????????_????????? ??????
        switchMotion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // ????????? ?????? ?????????
                if (isChecked) {
                    motionSensor = "yes";
                } else {
                    // ????????? ?????? ????????????
                    motionSensor = "no";
                }
            }
        });

        // ???????????????_????????? ??????
        switchMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // ????????? ?????? ?????????
                if (isChecked) {
                    guardianMessage = "yes";
                } else {
                    // ????????? ?????? ????????????
                    guardianMessage = "no";
                }
            }
        });

        // ???????????? ??????
        btnJoin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                userInfoToString();

                // ???????????? ?????? ????????? ?????? ???
                if (ID.equals("") || PW.equals("") || name.equals("") || phone.equals("") || phone2.equals("") || birth.equals("") || height.equals("")
                        || weight.equals("") || bloodType.equals("") || gender.equals("") || motionSensor.equals("") || guardianMessage.equals("")) {
                    
                    Toast.makeText(JoinActivity.this, "?????? ????????? ??????????????????.", Toast.LENGTH_LONG).show();
                }
                    
                // ?????? ????????? ??????????????? ???
                if (!ID.equals("") && !PW.equals("") && !name.equals("") && !phone.equals("") && !phone2.equals("") && !birth.equals("") && !height.equals("")
                        && !weight.equals("") && !bloodType.equals("") && !gender.equals("") && !motionSensor.equals("") && !guardianMessage.equals("")) {

                    // ????????? ?????? ??????
                    if (idCheck.equals("unavailable") || idCheck.equals("")) {
                        Toast.makeText(JoinActivity.this, "????????? ??????????????? ????????????.", Toast.LENGTH_LONG).show();
                    }
                    
                    // ???????????? != ???????????? ??????
                    if (pwCheck() == 0) {
                        
                        Toast.makeText(JoinActivity.this, "??????????????? ???????????? ????????????.", Toast.LENGTH_LONG).show();
                        Log.v(TAG, "PW: " + PW + ", PW2: " + PW2);
                    }

                    // ???????????? ?????? ??????
                    if (idCheck.equals("available") && pwCheck() == 1){
                        createUser(ID, PW, phone, phone2, name, birth, height, weight, bloodType, gender, motionSensor, guardianMessage);
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    // ???????????? toString
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

    // ???????????? ?????? ??????
    public int pwCheck() {
        int pwCheck;
        if (PW2.equals(PW)) {
            pwCheck = 1;
        } else
            pwCheck = 0;

        return pwCheck;
    }

    // ????????? ????????????
    private void idDuplicateCheck(String idCheck) {

        // ????????? ???????????? ????????? user??? ????????? ??????
        databaseReference.child("users").child(idCheck).child("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String idAvailability;
                idAvailability = (String) snapshot.getValue();

                if (idAvailability == null) {
                    Toast.makeText(JoinActivity.this, "????????? ??? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                    idAvailability = "available";
                    setIdCheck(idAvailability);
                } else {
                    idAvailability = "unavailable";
                    setIdCheck(idAvailability);
                    Log.v(TAG, "onDataChange_getIdValue: " + idAvailability);
                    Toast.makeText(JoinActivity.this, "?????? ???????????? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled_idDuplicateCheck: " + error.toException());
            }
        });

    }

    // set idCheck
    private void setIdCheck(String idAvailability) {
        idCheck = idAvailability;
    }

    // ????????????
    private void createUser(String ID, String PW, String phone, String phone2, String name, String birth,
                            String height, String weight, String bloodType, String gender, String motionSensor, String guardianMessage) {
        User user = new User(ID, PW, phone, phone2, name, birth, height, weight, bloodType, gender, motionSensor, guardianMessage);

        databaseReference.child("users").child(ID).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(JoinActivity.this, "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(JoinActivity.this, "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
