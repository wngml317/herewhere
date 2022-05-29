package com.inhatc.herewhere;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {

    private TextView txtID;
    private Button btnTag, btnMotion, btnMessage, btnSOS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        txtID = findViewById(R.id.txtID);
        Intent myIntent = getIntent(); /*데이터 수신*/

        String id = myIntent.getExtras().getString("id"); /*String형*/
        txtID.setText(id+"님");

        btnTag = findViewById(R.id.btnTag);
        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), NFCtagActivity.class);
                // 로그인 정보 같이 보내기
                myIntent.putExtra("id", id);
                startActivity(myIntent);
            }
        });

        btnMotion = findViewById(R.id.btnMotion);
        btnMotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), MotionActivity.class);
                // 로그인 정보 같이 보내기
                myIntent.putExtra("id", id);
                startActivity(myIntent);
            }
        });

        btnMessage = findViewById(R.id.btnMessage);
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), MessageActivity.class);
                // 로그인 정보 같이 보내기
                myIntent.putExtra("id", id);
                startActivity(myIntent);
            }
        });

        btnSOS = findViewById(R.id.btnSOS);

    }
}
