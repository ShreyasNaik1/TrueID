package com.trueid.trueid.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import com.trueid.trueid.model.Account;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * Created by junxiang92 on 8/10/16.
 */

public class Server {
    public static boolean sendFCM(Context c) {

        SharedPreferences pref = c.getSharedPreferences(Config.SHARED_PREF, 0);
        String code = pref.getString("regId", null);
        String phone = pref.getString("phone", null);
        if (phone == null)
            return false;

        JSONObject postData = new JSONObject();
        try {
            postData.put("token", code);
            postData.put("mobile_id", phone);
            Log.d("FCM Token", code);

            SendDeviceDetails sdd = new SendDeviceDetails();
            sdd.execute(Config.SERVER + "/api", postData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean sendCredentials(Context c, Account acc) {
        SharedPreferences pref = c.getSharedPreferences(Config.SHARED_PREF, 0);
        String phone = pref.getString("phone", null);

        JSONObject postData = new JSONObject();
        try {
            postData.put("mobile_id", phone);
            postData.put("domain_name", acc.getDomain());
            postData.put("username", acc.getUsername());
            postData.put("password", acc.getPassword());

            SendDeviceDetails sdd = new SendDeviceDetails();
            sdd.execute(Config.SERVER + "/api/message", postData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean sendVerification(String mobile, String code) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("mobile_id", mobile);
            postData.put("secret_key", code);

            SendDeviceDetails sdd = new SendDeviceDetails();
            if (code == null) {
                sdd.execute(Config.SERVER + "/api/sms", postData.toString());
                return true;
            } else {
                String json = sdd.execute(Config.SERVER + "/api/sms", postData.toString()).get();
                Log.d("JSON Data", json);
                JSONObject obj = new JSONObject(json);
                return obj.getJSONObject("success").getBoolean("matched");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return true;
    }
}

class SendDeviceDetails extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {

        String data = "";

        HttpsURLConnection httpURLConnection = null;
        try {

            httpURLConnection = (HttpsURLConnection) new URL(params[0]).openConnection();

            // Create the SSL connection
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, null, new java.security.SecureRandom());
            httpURLConnection.setSSLSocketFactory(sc.getSocketFactory());

            // httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            httpURLConnection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(params[1]);
            wr.flush();
            wr.close();

            InputStream in = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            int inputStreamData = inputStreamReader.read();
            while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                inputStreamData = inputStreamReader.read();
                data += current;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
    }
}