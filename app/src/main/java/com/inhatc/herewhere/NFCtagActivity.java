package com.inhatc.herewhere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NFCtagActivity extends AppCompatActivity {

    private TextView txtID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfctag);

        txtID = findViewById(R.id.txtID);
        SharedPreferences autoId = getSharedPreferences("id", MODE_PRIVATE);
        String id = autoId.getString("id", "");
//        Intent myIntent = getIntent(); /*데이터 수신*/
//        String id = myIntent.getExtras().getString("id"); /*String형*/
        txtID.setText(id+"님");

    }
}
