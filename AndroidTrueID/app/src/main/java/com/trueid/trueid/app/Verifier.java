package com.trueid.trueid.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.trueid.trueid.model.AccountDAO;

/**
 * Created by junxiang92 on 9/10/16.
 */

public class Verifier {
    public static AccountDAO verifyPin(Context c, String pin) {
        AccountDAO accDao = new AccountDAO(c);
        if (!accDao.open(pin))
            return null;

        return accDao;
    }
}
