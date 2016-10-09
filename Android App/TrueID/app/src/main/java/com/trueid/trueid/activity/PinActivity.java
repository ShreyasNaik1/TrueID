package com.trueid.trueid.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.trueid.trueid.R;
import com.trueid.trueid.app.Config;
import com.trueid.trueid.app.Server;
import com.trueid.trueid.model.AccountDAO;

/**
 * Created by junxiang92 on 9/10/16.
 */

public class PinActivity extends AppCompatActivity {
    private EditText pinTxt;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_pin);

        phone = getIntent().getStringExtra("phone");
        pinTxt = (EditText) findViewById(R.id.pinText);
    }

    public void onSetPin(View v) {
        String pin = pinTxt.getText().toString();

        if (pin.length() < 3) {
            Toast.makeText(v.getContext(), "Length of PIN should be more than 3.", Toast.LENGTH_LONG).show();
            return;
        }

        // Create a Database
        AccountDAO accDao = new AccountDAO(v.getContext());
        accDao.open(pin);
        accDao.close();

        // Login
        SharedPreferences settings = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, Context.MODE_PRIVATE); //1;
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean("registered", true);
        editor.putString("phone", phone);
        editor.commit();

        Server.sendFCM(this);

        Intent i = new Intent(this, AccountsActivity.class);
        i.putExtra("pin", pin);
        startActivity(i);
        finish();
    }

}
