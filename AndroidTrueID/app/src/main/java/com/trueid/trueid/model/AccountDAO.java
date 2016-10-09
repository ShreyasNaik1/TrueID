package com.trueid.trueid.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

public class AccountDAO implements Serializable {

    // Database fields
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private Context context;
    private String[] allColumns = {"_id", "icon", "name", "domain", "username", "password"};

    public AccountDAO(Context context) {
        this.context = context;
        SQLiteDatabase.loadLibs(context);

    }

    public boolean open(String pin) throws SQLException {
        try {
            database = new SQLiteHelper(context).getWritableDatabase(pin);
            return true;
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void close() {
        database.close();
    }

    public void changePin(String oldPin, String newPin) {
        database.rawExecSQL("PRAGMA key = '" + oldPin + "';");
        database.rawExecSQL("PRAGMA rekey = '" + newPin + "';");
    }


    public Account createAccount(Account acc) {
        long insertId = database.insert(SQLiteHelper.TABLE_NAME, null, toValues(acc));
        Cursor cursor = database.query(SQLiteHelper.TABLE_NAME, allColumns, SQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Account newAccount = cursorToAccount(cursor);
        cursor.close();
        return newAccount;
    }

    public void updateAccount(Account acc) {
        database.update(SQLiteHelper.TABLE_NAME, toValues(acc), SQLiteHelper.COLUMN_ID + " = " + acc.getId(), null);
    }

    public void deleteAccount(Account acc) {
        long id = acc.getId();
        System.out.println("Account deleted with id: " + id);
        database.delete(SQLiteHelper.TABLE_NAME, SQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public List<Account> getAllAccounts() {
        List<Account> Accounts = new ArrayList<Account>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Account Account = cursorToAccount(cursor);
            Accounts.add(Account);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return Accounts;
    }

    public Account getAccountByDomain(String domain) {
        Account acc = null;

        Cursor cursor = database.query(SQLiteHelper.TABLE_NAME,
                allColumns, "domain = '" + domain + "'", null, null, null, null);

        cursor.moveToFirst();
        if (!cursor.isAfterLast())
            acc = cursorToAccount(cursor);

        return acc;
    }

    public ContentValues toValues(Account acc) {
        ContentValues accMap = new ContentValues();
        accMap.put("icon", acc.getIcon());
        accMap.put("name", acc.getName());
        accMap.put("domain", acc.getDomain());
        accMap.put("username", acc.getUsername());
        accMap.put("password", acc.getPassword());

        return accMap;
    }

    private Account cursorToAccount(Cursor cursor) {
        Account acc = new Account();

        acc.setId(cursor.getLong(0));
        acc.setIcon(cursor.getString(1));
        acc.setName(cursor.getString(2));
        acc.setDomain(cursor.getString(3));
        acc.setUsername(cursor.getString(4));
        acc.setPassword(cursor.getString(5));

        return acc;
    }


}
