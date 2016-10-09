package com.trueid.trueid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trueid.trueid.R;
import com.trueid.trueid.model.Account;

/**
 * Created by junxiang92 on 8/10/16.
 */

public class AccountAdapter extends BaseAdapter {

    private Account accs[];
    private LayoutInflater inflater;

    public AccountAdapter(Context context, Account accs[]) {
        this.accs = accs;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return accs.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.accounts_one, parent, false);


        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        TextView name = (TextView) view.findViewById(R.id.nameTxt);
        TextView username = (TextView) view.findViewById(R.id.usernameTxt);

        Account acc = accs[position];
        // Update Icon
        // icon.setImageIcon(null);
        name.setText(acc.getName());
        username.setText(acc.getUsername());

        return view;
    }
}

