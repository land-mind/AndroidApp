package com.mymindsweeper.mymindsweeper.ui;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mymindsweeper.mymindsweeper.R;
import com.mymindsweeper.mymindsweeper.sms.SMSUtils;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SMSUtils.initREAD_SMS(this);
        String inbox = SMSUtils.readSMS(this);
        System.out.println("SMS inbox:");
        System.out.println(inbox);
    }

}
