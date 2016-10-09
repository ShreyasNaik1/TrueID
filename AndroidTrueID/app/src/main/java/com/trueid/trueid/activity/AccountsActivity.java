package com.trueid.trueid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import com.trueid.trueid.R;
import com.trueid.trueid.adapter.AccountAdapter;
import com.trueid.trueid.model.Account;
import com.trueid.trueid.model.AccountDAO;

/**
 * Created by junxiang92 on 8/10/16.
 */

public class AccountsActivity extends AppCompatActivity {

    private AccountDAO accDao;
    private String pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accounts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pin = getIntent().getStringExtra("pin");

        accDao = new AccountDAO(this);
        if(!accDao.open(pin)) {
            finish();
            return;
        }

        List<Account> acccList = accDao.getAllAccounts();
        accDao.close();
        final Account accs[] = acccList.toArray(new Account[acccList.size()]);

        ListView accList = (ListView) findViewById(R.id.accList);
        if (accs.length > 0) {
            accList.setAdapter(new AccountAdapter(this, accs));

            accList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(parent.getContext(), AccountEditActivity.class);
                    i.putExtra("account", accs[position]);
                    i.putExtra("pin", pin);
                    startActivity(i);
                }
            });
        } else {
            String values[] = {"Start adding your accounts with the + value above!"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
            accList.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.accounts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == R.id.action_add) {
            Intent i = new Intent(this, AccountEditActivity.class);
            i.putExtra("pin", pin);
            startActivity(i);
        } else if (item.getItemId() == R.id.action_settings){
            Intent i = new Intent(this, SettingsActivity.class);
            i.putExtra("pin", pin);
            startActivity(i);
        } else if (item.getItemId() == R.id.action_change_pin) {
            Intent i = new Intent(this, ChangePinActivity.class);
            i.putExtra("pin", pin);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
