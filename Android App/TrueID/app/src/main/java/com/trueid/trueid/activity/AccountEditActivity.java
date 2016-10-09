package com.trueid.trueid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

import com.trueid.trueid.R;
import com.trueid.trueid.model.Account;
import com.trueid.trueid.model.AccountDAO;

/**
 * Created by junxiang92 on 8/10/16.
 */

public class AccountEditActivity extends AppCompatActivity {
    private Account account;
    private String pin;
    private AccountDAO accDao;
    private EditText nameTxt, domainTxt, usernameTxt, passwordTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        pin = getIntent().getStringExtra("pin");
        accDao = new AccountDAO(this);
        if (!accDao.open(pin)) {
            finish();
            return;
        }

        nameTxt = (EditText) findViewById(R.id.nameTxt);
        domainTxt = (EditText) findViewById(R.id.domainTxt);
        usernameTxt = (EditText) findViewById(R.id.usernameTxt);
        passwordTxt = (EditText) findViewById(R.id.passwordTxt);

        account = (Account) getIntent().getSerializableExtra("account");
        String domain = getIntent().getStringExtra("domain");
        if (account != null) {
            nameTxt.setText(account.getName());
            domainTxt.setText(account.getDomain());
            usernameTxt.setText(account.getUsername());
            passwordTxt.setText(account.getPassword());
        } else if (domain != null) {
            domainTxt.setText(domain);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (account != null)
            getMenuInflater().inflate(R.menu.account_edit, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finishEdit(); // close this activity and return to preview activity (if there is any)
        } else if (item.getItemId() == R.id.action_delete) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            deleteAccount();
                            finishEdit();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to delete?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }

        return super.onOptionsItemSelected(item);
    }


    private void finishEdit() {
        Intent i = new Intent(this, AccountsActivity.class);
        i.putExtra("pin", pin);
        startActivity(i);
    }


    public void onGenPass(View v) {
        EditText passTxt = (EditText) findViewById(R.id.passwordTxt);
        passTxt.setInputType(InputType.TYPE_CLASS_TEXT);

        passTxt.setText(genRandom(16));
    }

    public static String genRandom(int randomLength) {
        char randomVal[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()".toCharArray();

        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = randomVal[generator.nextInt(randomVal.length)];
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    private void deleteAccount() {
        accDao.deleteAccount(account);
    }

    public void onSave(View v) {
        // Insert else Edit
        if (account == null) {
            Account acc = new Account("icon", nameTxt.getText().toString(), domainTxt.getText().toString(), usernameTxt.getText().toString(), passwordTxt.getText().toString());
            accDao.createAccount(acc);
        } else {
            account.setName(nameTxt.getText().toString());
            account.setDomain(domainTxt.getText().toString());
            account.setUsername(usernameTxt.getText().toString());
            account.setPassword(passwordTxt.getText().toString());

            accDao.updateAccount(account);
        }

        Toast.makeText(this, "Account has been saved successfully.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishEdit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
