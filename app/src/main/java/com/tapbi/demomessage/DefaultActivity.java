package com.tapbi.demomessage;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class DefaultActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_Agree, btn_Reject;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        
        btn_Reject = findViewById(R.id.btn_reject);
        btn_Reject.setOnClickListener(this);

        btn_Agree = findViewById(R.id.btn_agree);
        btn_Agree.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_agree:
                changeDefault();
                break;
            case R.id.btn_reject:
                finish();
                break;
        }
    }

    private void changeDefault() {
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        String myPackageName = getPackageName();
        if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
            btn_Agree.setVisibility(View.VISIBLE);
        } else {
            btn_Reject.setVisibility(View.GONE);
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }
}