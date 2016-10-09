package com.trueid.trueid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.trueid.trueid.R;
import com.trueid.trueid.model.AccountDAO;

/**
 * Created by junxiang92 on 9/10/16.
 */

public class ChangePinActivity extends AppCompatActivity {
    private String pin;
    private EditText oldPinTxt, newPinTxt, reNewPinTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_pin);

        pin = getIntent().getStringExtra("pin");
        oldPinTxt = (EditText) findViewById(R.id.oldPin);
        newPinTxt = (EditText) findViewById(R.id.newPin);
        reNewPinTxt = (EditText) findViewById(R.id.reNewPin);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, AccountsActivity.class);
        i.putExtra("pin", pin);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finishAffinity();
    }

    public void onChangePin(View v) {
        String oldPin = oldPinTxt.getText().toString();
        String newPin = newPinTxt.getText().toString();
        String reNewPin = reNewPinTxt.getText().toString();

        if (!oldPin.equals(pin)) {
            Toast.makeText(v.getContext(), "Old Pin is Invalid.", Toast.LENGTH_LONG).show();
            return;
        }

        if (newPin.length() == 0 || !newPin.equals(reNewPin)) {
            Toast.makeText(v.getContext(), "New Pin cannot be empty or Pin are not equal.", Toast.LENGTH_LONG).show();
            return;
        }

        AccountDAO accDao = new AccountDAO(this);
        accDao.open(oldPin);
        accDao.changePin(oldPin, newPin);
        accDao.close();
        Toast.makeText(v.getContext(), "Pin has been changed successfully.", Toast.LENGTH_LONG).show();
    }
}
