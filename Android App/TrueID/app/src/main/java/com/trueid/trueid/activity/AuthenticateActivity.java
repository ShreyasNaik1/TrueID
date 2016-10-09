package com.trueid.trueid.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.trueid.trueid.R;
import com.trueid.trueid.app.Config;
import com.trueid.trueid.app.Server;

/**
 * Created by junxiang92 on 9/10/16.
 */

public class AuthenticateActivity extends AppCompatActivity {
    private TextView phoneNum;
    private EditText pinTxt;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_code);

        checkRegistered();

        phone = getIntent().getStringExtra("phone");
        phoneNum = (TextView) findViewById(R.id.phoneNum);
        pinTxt = (EditText) findViewById(R.id.pinTxt);

        phoneNum.setText(phone);
    }

    private void checkRegistered() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, Context.MODE_PRIVATE); //1;
        if (settings.getBoolean("registered", false)) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

    public void onValidate(View v) {
        String code = pinTxt.getText().toString();
        boolean success = Server.sendVerification(phone, code);
        if (success) {
            Intent i = new Intent(v.getContext(), PinActivity.class);
            i.putExtra("phone", phone);
            startActivity(i);
            return;
        }

        Toast.makeText(v.getContext(), "You have entered an invalid code. Please try again.", Toast.LENGTH_LONG).show();
    }
}
