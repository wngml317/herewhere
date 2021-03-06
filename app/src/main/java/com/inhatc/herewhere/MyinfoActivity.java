package com.inhatc.herewhere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.kyleduo.switchbutton.SwitchButton;

public class MyinfoActivity extends AppCompatActivity {

    private static final String TAG = "MyinfoActivity";

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    TextView userID;
    TextView userName;
    TextView userPhone;
    TextView userGuardianPhone;
    TextView userHeight;
    TextView userWeight;
    TextView userBloodType;
    TextView userBirth;
    TextView userGender;
    Button btnModify;
    ImageView userImg;

    SwitchButton switchMotion;
    SwitchButton switchMessage;

    String motionSensor;
    String guardianMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);

        SharedPreferences autoId = getSharedPreferences("id", MODE_PRIVATE);
        String loginID = autoId.getString("id", "");
//        Intent intent = getIntent();
//        String loginID = intent.getExtras().getString("id");

        userID = findViewById(R.id.txtUserID);
        userName = findViewById(R.id.txtUserName);
        userPhone = findViewById(R.id.txtUserPhone);
        userGuardianPhone = findViewById(R.id.txtGuardianPhone);
        userHeight = findViewById(R.id.txtUserHeight);
        userWeight = findViewById(R.id.txtUserWeight);
        userBloodType = findViewById(R.id.txtUserBloodType);
        userBirth = findViewById(R.id.txtUserBirth);
        userGender = findViewById(R.id.txtUserGender);
        btnModify = findViewById(R.id.btnModify);
        userImg = findViewById(R.id.imgUser);
        switchMotion = findViewById(R.id.switchMotion);
        switchMessage = findViewById(R.id.switchMessage);

        // ????????? ????????? ?????? uri
        String userUri = loginID + ".png";
        // ????????? ????????? ???????????? ??????
        displayProfileImg(userUri);
        // ??????????????? ???????????? ??????
        displayUserinfo(loginID);

        switchMotion.setEnabled(false);
        switchMessage.setEnabled(false);

        // ???????????? ?????? ?????? ?????????
        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UpdateinfoActivity.class);
                intent.putExtra("id", loginID);
                startActivity(intent);
            }
        });

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
                Toast.makeText(getApplicationContext(), "????????? ?????? ????????? ??????", Toast.LENGTH_SHORT).show();
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
                    userBloodType.setText(user.getBloodType());
                    userBirth.setText(user.getBirth());
                    userGender.setText(user.getGender());

                    motionSensor = (String) user.getMotionSensor();
                    if (motionSensor.equals("yes")) {
                        switchMotion.setChecked(true);
                    } else {
                        switchMotion.setChecked(false);
                    }

                    guardianMessage = (String) user.getGuardianMessage();
                    if (guardianMessage.equals("yes")) {
                        switchMessage.setChecked(true);
                    } else {
                        switchMessage.setChecked(false);
                    }
                }
            }
        });
    }
}
