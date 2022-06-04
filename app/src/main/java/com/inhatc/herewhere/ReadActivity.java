package com.inhatc.herewhere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ReadActivity extends AppCompatActivity {

    TextView txtID;
    Button btnInfo;
    Button btnSOS;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        txtID = findViewById(R.id.txtID);
//        Intent myIntent = getIntent(); /*데이터 수신*/

        SharedPreferences autoId = getSharedPreferences("id", MODE_PRIVATE);
        String id = autoId.getString("id", "");
//        String id = myIntent.getExtras().getString("id"); /*String형*/
        txtID.setText(id+"님");

        // 내정보 수정
        btnInfo = findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyinfoActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        // 구조요청
        btnSOS = findViewById(R.id.btnSOS);
        btnSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), Activity.class );
//                startActivity(intent);
            }
        });
    }
}
