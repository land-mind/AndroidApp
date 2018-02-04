package com.mymindsweeper.mymindsweeper.sms;

import android.app.Activity;

import com.mymindsweeper.mymindsweeper.ui.Home;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SMSOutgoingDetector extends Thread {

    private static SMSText lastSentSMS;

    public SMSOutgoingDetector(Activity activity) {
        super(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    SMSText poll = SMSUtils.mostRecentSMS(activity, SMSUtils.SENT);
                    if(poll.toString().equals(lastSentSMS.toString())) {
                    } else {
                        JSONObject jsonParam = new JSONObject();
                        try {
                            jsonParam.put("token", Home.token);
                            jsonParam.put("text", poll.getBody());
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
                        lastSentSMS = poll;
                    }
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {

                    }
                }
            }
        });
        lastSentSMS = SMSUtils.mostRecentSMS(activity, SMSUtils.SENT);
    }

}