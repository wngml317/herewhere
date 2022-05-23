package com.inhatc.herewhere;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.StorageReference;

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
    EditText userGender;
    Button btnSave;
    ImageView userImg;

    Spinner spinner;
    String bloodType;
    int bloodTypeIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateinfo);

        userID = findViewById(R.id.txtUserID);
        userName = findViewById(R.id.txtUserName);
        userPhone = findViewById(R.id.txtUserPhone);
        userGuardianPhone = findViewById(R.id.txtGuardianPhone);
        userHeight = findViewById(R.id.txtUserHeight);
        userWeight = findViewById(R.id.txtUserWeight);
        userBirth = findViewById(R.id.txtUserBirth);
        userGender = findViewById(R.id.txtUserGender);
        btnSave = findViewById(R.id.btnModify);
        userImg = findViewById(R.id.imgUser);
        spinner = findViewById(R.id.spinner);

        // 임시 아이디
        String testID = "1sunny";
        // 사용자 프로필 사진 uri
        String userUri = testID + ".png";
        // 프로필 사진을 나타내는 함수
        displayProfileImg(userUri);
        // 개인정보를 나타내는 함수
        displayUserinfo(testID);
    }

    private int getBloodIndex(String strBloodType){
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
                Toast.makeText(getApplicationContext(), "프로필 사진 업로드 필요", Toast.LENGTH_SHORT).show();
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
                    userGender.setText(user.getGender());
                    bloodTypeIndex = getBloodIndex(user.getBloodType());
                    spinner.setSelection(bloodTypeIndex);
                }
            }
        });
    }
}
