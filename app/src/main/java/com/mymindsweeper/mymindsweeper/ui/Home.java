package com.mymindsweeper.mymindsweeper.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mymindsweeper.mymindsweeper.R;
import com.mymindsweeper.mymindsweeper.sms.SMSText;
import com.mymindsweeper.mymindsweeper.sms.SMSUtils;

import java.util.List;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SMSUtils.initREAD_SMS(this);
        String inbox = SMSUtils.readSMS(this, SMSUtils.INBOX);
        List<SMSText> inboxTexts = SMSText.stringToArray(inbox, SMSText.SMSType.INBOX);
        String sent = SMSUtils.readSMS(this, SMSUtils.SENT);
        List<SMSText> sentTexts = SMSText.stringToArray(sent, SMSText.SMSType.SENT);
    }

}
