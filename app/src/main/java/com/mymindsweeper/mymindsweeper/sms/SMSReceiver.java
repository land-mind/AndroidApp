package com.mymindsweeper.mymindsweeper.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.mymindsweeper.mymindsweeper.ui.Home;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();

        if (intentExtras != null) {
            /* Get Messages */
            Object[] sms = (Object[]) intentExtras.get("pdus");

            for (int i = 0; i < sms.length; ++i) {
                /* Parse Each Message */
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String phone = smsMessage.getOriginatingAddress();
                String message = smsMessage.getMessageBody().toString();

                JSONObject jsonParam = new JSONObject();
                try {
                    jsonParam.put("token", Home.token);
                    jsonParam.put("text", message);
                } catch (JSONException e) {
                    System.out.println(e);
                }
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("http://54.163.167.120:5000/sweep");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                            conn.setRequestProperty("Accept", "application/json");
                            conn.setDoOutput(true);
                            conn.setDoInput(true);

                            DataOutputStream os = new DataOutputStream(conn.getOutputStream());

                            os.writeBytes(jsonParam.toString());
                            os.flush();
                            os.close();

                            BufferedReader in = new BufferedReader(new InputStreamReader(
                                    conn.getInputStream()));

                            StringBuilder res = new StringBuilder();
                            String inputLine;
                            while ((inputLine = in.readLine()) != null)
                                res.append(inputLine);
                            if(res.toString().contains("trigger")) {
                                SMSUtils.sendSMS("I'm feeling down today. --Sent by mymindsweeper--");
                            }
                            in.close();
                            conn.disconnect();
                        } catch(Exception e) {
                            System.out.println(e);
                        }
                    }
                });
                thread.start();
            }
        }
    }
}