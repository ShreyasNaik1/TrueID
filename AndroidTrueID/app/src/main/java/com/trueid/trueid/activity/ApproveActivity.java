package com.trueid.trueid.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.trueid.trueid.R;
import com.trueid.trueid.app.Config;
import com.trueid.trueid.app.Server;
import com.trueid.trueid.app.Verifier;
import com.trueid.trueid.model.Account;
import com.trueid.trueid.model.AccountDAO;

public class ApproveActivity extends AppCompatActivity {
    private TextView txtMessage;
    private String domain;
    private String pin = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approve);

        txtMessage = (TextView) findViewById(R.id.txt_push_message);

        SharedPreferences settings = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, Context.MODE_PRIVATE);
        pin = settings.getString("pin", null);
        if (pin != null) {
            findViewById(R.id.textView2).setVisibility(View.GONE);
            findViewById(R.id.pinText).setVisibility(View.GONE);
        }

        domain = getIntent().getStringExtra("message");
        txtMessage.setText(domain);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public void onReject(View v) {
        finish();
    }

    public void onAccept(final View v) {
        EditText pinTxt = (EditText) findViewById(R.id.pinText);
        final String pin1 = (pin != null) ? pin : pinTxt.getText().toString();
        AccountDAO accDao = Verifier.verifyPin(getApplicationContext(), pin1);

        if (accDao == null) {
            Toast.makeText(getApplicationContext(), "Your pin is invalid.", Toast.LENGTH_LONG).show();
            return;
        }

        Account acc = accDao.getAccountByDomain(domain);
        if (acc == null) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            if (pin1 != null) {
                                Intent i = new Intent(v.getContext(), AccountEditActivity.class);
                                i.putExtra("domain", domain);
                                i.putExtra("pin", pin1);
                                startActivity(i);
                            } else {
                                Intent i = new Intent(v.getContext(), LoginActivity.class);
                                startActivity(i);
                            }
                            break;
                    }
                    finish();
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This Account Information is not stored. Do you want to add one?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
            return;
        }

        Server.sendCredentials(getApplicationContext(), acc);
        Toast.makeText(getApplicationContext(), "Your credentials have been sent!", Toast.LENGTH_LONG).show();
        finishAffinity();
    }
}
