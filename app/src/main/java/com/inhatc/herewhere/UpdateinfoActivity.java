package com.inhatc.herewhere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kyleduo.switchbutton.SwitchButton;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateinfoActivity extends AppCompatActivity {

    private static final String TAG = "UpdateinfoActivity";

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    TextView userID;
    EditText userName;
    EditText userPhone;
    EditText userGuardianPhone;
    EditText userHeight;
    EditText userWeight;
    EditText userBirth;
    Button btnSave;
    ImageView userImg;

    Spinner spinner;
    String bloodType;
    int bloodTypeIndex;

    RadioGroup radioGroup;
    RadioButton radioBtnMan;
    RadioButton radioBtnWoman;
    SwitchButton switchMotion;
    SwitchButton switchMessage;

    Uri filePath;

    String ID;
    String PW;
    String phone;
    String guardianPhone;
    String name;
    String birth;
    String height;
    String weight;
    String gender;
    String motionSensor;
    String guardianMessage;

    String loginID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateinfo);

        SharedPreferences autoId = getSharedPreferences("id", MODE_PRIVATE);
        loginID = autoId.getString("id", "");
        Intent intent = getIntent();
//        String loginID = intent.getExtras().getString("id");

        userID = findViewById(R.id.txtUserID);
        userName = findViewById(R.id.txtUserName);
        userPhone = findViewById(R.id.txtUserPhone);
        userGuardianPhone = findViewById(R.id.txtGuardianPhone);
        userHeight = findViewById(R.id.txtUserHeight);
        userWeight = findViewById(R.id.txtUserWeight);
        userBirth = findViewById(R.id.txtUserBirth);
        radioGroup = findViewById(R.id.Radiogroup);
        btnSave = findViewById(R.id.btnSave);
        userImg = findViewById(R.id.imgUser);
        spinner = findViewById(R.id.spinner);
        radioBtnMan = findViewById(R.id.btnMan);
        radioBtnWoman = findViewById(R.id.btnWoman);
        switchMotion = findViewById(R.id.switchMotion);
        switchMessage = findViewById(R.id.switchMessage);

        // ????????? ????????? ?????? uri
        String userUri = loginID + ".png";
        // ????????? ????????? ???????????? ??????
        displayProfileImg(userUri);
        // ??????????????? ???????????? ??????
        displayUserinfo(loginID);

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

        // ??????
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

        // ????????? ?????? ????????????
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

        // ????????? ?????? ?????? ????????????
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

        // ????????? ?????? ?????????
        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "???????????? ???????????????."), 0);
            }
        });

        // ???????????? ?????? ?????? ?????????
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEditTextData();

                // ???????????? ?????? ????????? ?????? ???
                if (ID.equals("") || PW.equals("") || name.equals("") || phone.equals("") || guardianPhone.equals("") || birth.equals("") || height.equals("")
                        || weight.equals("") || bloodType.equals("") || gender.equals("") || motionSensor.equals("") || guardianMessage.equals("")) {

                    Toast.makeText(UpdateinfoActivity.this, "?????? ????????? ??????????????????.", Toast.LENGTH_LONG).show();
                }

                // ?????? ????????? ??????????????? ???
                if (!ID.equals("") && !PW.equals("") && !name.equals("") && !phone.equals("") && !guardianPhone.equals("") && !birth.equals("") && !height.equals("")
                        && !weight.equals("") && !bloodType.equals("") && !gender.equals("") && !motionSensor.equals("") && !guardianMessage.equals("")) {
                    uploadFile(loginID);
                    updateUser(ID, PW, phone ,guardianPhone, name, birth, height, weight, bloodType, gender, motionSensor, guardianMessage);
                }
            }
        });
    }

    private void uploadFile(String loginID) {
        if (filePath != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            String filename = loginID + ".png";
            StorageReference storageRef = storage.getReferenceFromUrl("gs://herewhere-468e5.appspot.com").child("profileImg/" + filename);
            storageRef.putFile(filePath)
                    //?????????
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "?????? ????????? ??????");
                        }
                    })
                    //?????????
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "?????? ????????? ??????");
                        }
                    })
                    //?????????
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "?????? ????????? ?????????...");
                        }
                    });
        } else {
            Log.d(TAG, "???????????? ?????? ??????");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            Log.d(TAG, "uri:" + String.valueOf(filePath));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                userImg.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                userImg.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void updateUser(String ID, String PW, String phone, String phone2, String name, String birth,
                            String height, String weight, String bloodType, String gender, String motionSensor, String guardianMessage) {
        User user = new User(ID, PW, phone, phone2, name, birth, height, weight, bloodType, gender, motionSensor, guardianMessage);

        databaseReference.child("users").child(ID).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UpdateinfoActivity.this, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);

                        // ????????? ?????? ?????? ?????? ?????? ??? MotionSensorService ??????
                        if (motionSensor.equals("yes")) {
                            startMotionSensorService("yes");
                        } else {
                            stopMotionSensorService("no");
                        }

                        // ????????? ?????? ?????? ?????? ?????? ?????? ??? herewhereService ??????
                        if (guardianMessage.equals("yes")) {
                            startMessageService("yes");
                        } else {
                            stopMessageService("no");
                        }

//                        intent.putExtra("id", ID);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateinfoActivity.this, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getEditTextData(){
        ID = userID.getText().toString();
        name = userName.getText().toString();
        phone = userPhone.getText().toString();
        guardianPhone = userGuardianPhone.getText().toString();
        height = userHeight.getText().toString();
        weight = userWeight.getText().toString();
        birth = userBirth.getText().toString();
    }

    public void checkGenderRadio(String strGender){
        if(strGender.equals("??????")){
            radioBtnMan.setChecked(true);
        }else if(strGender.equals("??????")){
            radioBtnWoman.setChecked(true);
        }else{
            radioBtnMan.setChecked(true);
        }
    }

    public int getBloodIndex(String strBloodType){
        if(strBloodType.equals("RH+ A")){
            return 0;
        }else if(strBloodType.equals("RH+ AB")){
            return 1;
        }else if(strBloodType.equals("RH+ B")){
            return 2;
        }else if(strBloodType.equals("RH+ O")){
            return 3;
        }else if(strBloodType.equals("RH- A")){
            return 4;
        }else if(strBloodType.equals("RH- AB")){
            return 5;
        }else if(strBloodType.equals("RH- B")){
            return 6;
        }else if(strBloodType.equals("RH- O")){
            return 7;
        }else{
            return 0;
        }
    }

    private void displayProfileImg(String userUri){
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://herewhere-468e5.appspot.com");
        StorageReference storageRef = storage.getReference("profileImg");
        storageRef.child(userUri).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(userImg);
                userImg.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "????????? ?????? ??????");
            }
        });
    }

    private void displayUserinfo(String loginID){
        databaseReference.child("users").child(loginID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "Error getting data", task.getException());
                }
                else {
                    Log.d(TAG, String.valueOf(task.getResult().getValue()));
                    User user = task.getResult().getValue(User.class);
                    userID.setText(user.getID());
                    userName.setText(user.getName());
                    userPhone.setText(user.getPhone());
                    userGuardianPhone.setText(user.getPhone2());
                    userHeight.setText(user.getHeight());
                    userWeight.setText(user.getWeight());
                    userBirth.setText(user.getBirth());
                    gender = String.valueOf(user.getGender());
                    checkGenderRadio(user.getGender());
                    bloodTypeIndex = getBloodIndex(user.getBloodType());
                    spinner.setSelection(bloodTypeIndex);

                    motionSensor = (String)user.getMotionSensor();
                    if (motionSensor.equals("yes")) {
                        switchMotion.setChecked(true);
                    } else {
                        switchMotion.setChecked(false);
                    }

                    guardianMessage = (String)user.getGuardianMessage();
                    if (guardianMessage.equals("yes")) {
                        switchMessage.setChecked(true);
                    } else {
                        switchMessage.setChecked(false);
                    }

                    // ??????????????? UpdateinfoActivity?????? ?????? ??????
                    PW = String.valueOf(user.getPW());
                }
            }
        });
    }

    // MotionSensorService ??????
    public void startMotionSensorService(String motionSensor_val) {
        Intent serviceIntent = new Intent(this, MotionSensorService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
            Log.v(TAG, "Build.VERSION.SDK_INT >= Build.VERSION_CODES.0: " + "????????? ?????? ??????");
        } else startService(serviceIntent);
    }

    // MotionSensorService ??????
    public void stopMotionSensorService(String motionSensor_val) {
        Intent serviceIntent = new Intent(this, MotionSensorService.class);
        stopService(serviceIntent);
    }

    // MessageService ??????
    public void startMessageService(String motionSensor_val) {
        Intent serviceIntent = new Intent(this, MessageService.class);
        serviceIntent.putExtra("id", loginID);
        startService(serviceIntent);
    }

    // MessageService ??????
    public void stopMessageService(String motionSensor_val) {
        Intent serviceIntent = new Intent(this, MessageService.class);
        stopService(serviceIntent);
    }
}
