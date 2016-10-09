package com.trueid.trueid.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.trueid.trueid.R;
import com.trueid.trueid.app.Config;
import com.trueid.trueid.app.Server;
import com.trueid.trueid.app.Verifier;
import com.trueid.trueid.model.AccountDAO;
import com.trueid.trueid.util.NotificationUtils;

/**
 * Created by junxiang92 on 8/10/16.
 */

public class LoginActivity extends AppCompatActivity{

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isNoPin;
    private AccountDAO accDao = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isRegistered()) {
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
            finish();
            return;
        }

        isNoPin = getIntent().getBooleanExtra("isNoPin", false);
        if (isNoPin)
            sendWarning();
        else
            Server.sendFCM(this);

        String message = getIntent().getStringExtra("message");
        getIntent().removeExtra("message");
        if (message != null) {
            startApproveActivity(getApplicationContext(), message);
            // finish();
        }
        startReceivers();

        setContentView(R.layout.login);
    }

    private void sendWarning() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_NEGATIVE:
                        finish();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This PIN will be stored on your device and it is may be targeted by hackers. Do you wish to continue?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void startReceivers() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                    Log.e("Firebase ID", pref.getString("regId", null));
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    startApproveActivity(context, intent.getStringExtra("message"));
                }
            }
        };
    }

    private void startApproveActivity(Context context, String message) {
        // new push notification is received
        Intent i = new Intent(context, ApproveActivity.class);
        i.putExtra("message", message);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        if (accDao != null)
            accDao.close();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    public boolean isRegistered() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, Context.MODE_PRIVATE); //1;
        return settings.getBoolean("registered", false);
    }

    public void onLogin(View v) {

        EditText pinTxt = (EditText) findViewById(R.id.pinText);
        String pin = pinTxt.getText().toString();
        accDao = Verifier.verifyPin(getApplicationContext(), pin);

        if (accDao != null) {
            if (!isNoPin) {
                Intent i = new Intent(this, AccountsActivity.class);
                i.putExtra("pin", pin);
                startActivity(i);
                finish();
            } else {
                SharedPreferences settings = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("pin", pin);
                editor.commit();
                setResult(Activity.RESULT_OK);
                Toast.makeText(this, "Pin has been saved on the device.", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Invalid PIN", Toast.LENGTH_LONG).show();
        }
    }
}
