package com.mymindsweeper.mymindsweeper.sms;

import android.app.Activity;

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