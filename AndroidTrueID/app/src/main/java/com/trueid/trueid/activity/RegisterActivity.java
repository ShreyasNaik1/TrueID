package com.trueid.trueid.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.trueid.trueid.R;
import com.trueid.trueid.app.Config;
import com.trueid.trueid.app.Server;
import com.trueid.trueid.model.AccountDAO;

public class RegisterActivity extends AppCompatActivity {
    EditText phoneTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkRegistered();


        setContentView(R.layout.register);

        phoneTxt = (EditText) findViewById(R.id.phoneText);
    }

    private void checkRegistered() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, Context.MODE_PRIVATE); //1;
        if (settings.getBoolean("registered", false)) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

    public void onRegister(View v){
        // Connect Server Code
        String phone = phoneTxt.getText().toString();

        if (Server.sendVerification(phone, null)) {
            Intent i = new Intent(v.getContext(), AuthenticateActivity.class);
            i.putExtra("phone", phone);
            startActivity(i);
            return;
        }

        Toast.makeText(v.getContext(), "Some error occured with the server.", Toast.LENGTH_LONG).show();
    }
}
