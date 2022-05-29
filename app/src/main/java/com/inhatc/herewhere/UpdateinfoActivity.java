package com.inhatc.herewhere;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.io.IOException;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateinfo);

        Intent intent = getIntent();
        String loginID = intent.getExtras().getString("id");

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

        // 사용자 프로필 사진 uri
        String userUri = loginID + ".png";
        // 프로필 사진을 나타내는 함수
        displayProfileImg(userUri);
        // 개인정보를 나타내는 함수
        displayUserinfo(loginID);

        // 혈액형
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

        // 성별
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

        // 이미지 클릭 이벤트
        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });

        // 저장하기 버튼 클릭 이벤트
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEditTextData();

                // 입력하지 않은 항목이 있을 때
                if (ID.equals("") || PW.equals("") || name.equals("") || phone.equals("") || guardianPhone.equals("") ||
                        birth.equals("") || height.equals("") || weight.equals("") || bloodType.equals("") || gender.equals("")) {

                    Toast.makeText(UpdateinfoActivity.this, "모든 항목을 입력해주세요.", Toast.LENGTH_LONG).show();
                }

                // 모든 항목을 입력하였을 때
                if (!ID.equals("") && !PW.equals("") && !name.equals("") && !phone.equals("") && !guardianPhone.equals("") && !birth.equals("")
                        && !height.equals("") && !weight.equals("") && !bloodType.equals("") && !gender.equals("")) {
                    uploadFile(loginID);
                    updateUser(ID, PW, phone ,guardianPhone, name, birth, height, weight, bloodType, gender);
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
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "파일 업로드 성공");
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "파일 업로드 실패");
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "파일 업로드 진행중...");
                        }
                    });
        } else {
            Log.d(TAG, "업로드할 파일 없음");
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
                            String height, String weight, String bloodType, String gender) {
        User user = new User(ID, PW, phone, phone2, name, birth, height, weight, bloodType, gender);

        databaseReference.child("users").child(ID).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UpdateinfoActivity.this, "수정을 완료하였습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        intent.putExtra("id", ID);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateinfoActivity.this, "수정에 실패하였습니다.", Toast.LENGTH_SHORT).show();
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
        if(strGender.equals("남자")){
            radioBtnMan.setChecked(true);
        }else if(strGender.equals("여자")){
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
                Log.d(TAG, "프로필 사진 없음");
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
                    // 비밀번호는 UpdateinfoActivity에서 수정 불가
                    PW = String.valueOf(user.getPW());
                }
            }
        });
    }
}
