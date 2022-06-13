package com.inhatc.herewhere;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class NFCtagActivity extends AppCompatActivity {

    public static final String TAG = NFCtagActivity.class.getSimpleName();

    private TextView txtID;
    private TextView nfc_message;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfctag);

        nfc_message = findViewById(R.id.txtTagMsg);

        txtID = findViewById(R.id.txtID);
        SharedPreferences autoId = getSharedPreferences("id", MODE_PRIVATE);
        String id = autoId.getString("id", "");
//        Intent myIntent = getIntent(); /*데이터 수신*/
//        String id = myIntent.getExtras().getString("id"); /*String형*/
        txtID.setText(id+"님");

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent targetIntent = new Intent(this, NFCtagActivity.class);

        //태그를 계속 인식할 때 새로운 Activity를 만들지 않고 현재 Activity에서 Intent를 받기 위해서
        targetIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent = PendingIntent.getActivity(this, 0, targetIntent, 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }

        mFilters = new IntentFilter[] { ndef, };

        mTechLists = new String[][] { new String[] { NfcF.class.getName() } };

        Intent passedIntent = getIntent();
        if (passedIntent != null) {
            String action = passedIntent.getAction();
            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
                processTag(passedIntent);
            }
        }
    }

    public void onResume() {
        super.onResume();

        if (mAdapter != null) {
            mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                    mTechLists);
        }
    }

    public void onPause() {
        super.onPause();

        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }


    public void onNewIntent(Intent passedIntent) {
        super.onNewIntent(passedIntent);
        // 전달받은 Intent에서 NFC 태그의 고유 ID를 얻음
        Tag tag = passedIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            Ndef ndef = Ndef.get(tag);
            readFromNFC(ndef);


        }
        // 전달받은 Intent에서 NFC 태그에 등록한 비즈니스 정보(사원코드 등)를 얻음
        if (passedIntent != null) {
            processTag(passedIntent); // processTag 메소드 호출
        }
    }



    private void processTag(Intent passedIntent) {
        Parcelable[] rawMsgs = passedIntent
                .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMsgs == null) {
            return;
        }

        Toast.makeText(getApplicationContext(), "스캔 성공!", Toast.LENGTH_SHORT).show();


        Log.i("info", "rawMsgs.length:"+rawMsgs.length);
        // 참고! rawMsgs.length : 스캔한 태그 개수
        NdefMessage[] msgs;
        if (rawMsgs != null) {

            msgs = new NdefMessage[rawMsgs.length];
            for (int i = 0; i < rawMsgs.length; i++) {
                msgs[i] = (NdefMessage) rawMsgs[i];

            }
        }
    }

    private void readFromNFC(Ndef ndef) {

        try {
            ndef.connect();
            NdefMessage ndefMessage = ndef.getNdefMessage();
            String message = new String(ndefMessage.getRecords()[0].getPayload());
            Log.d(TAG, "readFromNFC: "+message);
            nfc_message.setText(message);
            ndef.close();

        } catch (IOException | FormatException e) {
            e.printStackTrace();

        }
    }



}
