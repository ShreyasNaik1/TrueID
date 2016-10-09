package com.trueid.trueid.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

import com.trueid.trueid.R;
import com.trueid.trueid.app.Config;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Context context;
    private SharedPreferences sp;
    private MyPreferenceFragment prefFrag;
    private String pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefFrag = new MyPreferenceFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, prefFrag).commit();
        context = this.getApplicationContext();
        pin = getIntent().getStringExtra("pin");
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
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

//        finish();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        sp = sharedPreferences;
        if (sharedPreferences.getBoolean(key, false)) {
            PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
            Intent i = new Intent(context, LoginActivity.class);
            i.putExtra("isNoPin", true);
            startActivityForResult(i, 88);
        } else {
            SharedPreferences settings = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("pin");
            editor.commit();
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 88:
                if (resultCode != RESULT_OK) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("prefSavePin", false);
                    editor.commit();
                    Intent intent = new Intent(context, SettingsActivity.class);
                    finish();
                    startActivity(intent);
                    // prefFrag.getPreferenceScreen().findPreference("prefSavePin").setEnabled(false);//.setDefaultValue(false);
                }
                break;
        }
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

        }
    }

}
