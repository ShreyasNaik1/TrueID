package com.trueid.trueid.model;

import android.content.Context;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "accounts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMNS[] = {"icon", "name", "domain", "username", "password"};

    private static final String DATABASE_NAME = "accounts.db";
    private static final int DATABASE_VERSION = 1;
    private static SQLiteHelper instance;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "( " + COLUMN_ID
            + " integer primary key autoincrement";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static public synchronized SQLiteHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SQLiteHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        StringBuilder sb = new StringBuilder();
        sb.append(DATABASE_CREATE);

        for (String col: COLUMNS) {
            sb.append(", ");
            sb.append(col);
            sb.append(" text not null");
        }

        sb.append(");");
        database.execSQL(sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}