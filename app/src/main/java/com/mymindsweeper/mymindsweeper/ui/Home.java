package com.mymindsweeper.mymindsweeper.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mymindsweeper.mymindsweeper.R;
import com.mymindsweeper.mymindsweeper.sms.SMSText;
import com.mymindsweeper.mymindsweeper.sms.SMSUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getAllSMSThreads();
    }

    public void getAllSMSThreads() {
        SMSUtils.initREAD_SMS(this);
        List<SMSText> inboxTexts = SMSUtils.readSMS(this, SMSUtils.INBOX);
        List<SMSText> sentTexts = SMSUtils.readSMS(this, SMSUtils.SENT);

        List<SMSText> allMsgs = new ArrayList<SMSText>();

        allMsgs.addAll(inboxTexts);
        allMsgs.addAll(sentTexts);

        //group text by their thread ids
        Map<Integer, List<SMSText>> threads = allMsgs.stream().collect(Collectors.groupingBy(SMSText::getThreadId));

        List<SMSText> exampleTextThread = threads.get(1).stream().sorted(Comparator.comparing(SMSText::getDate)).collect(Collectors.toList());
        for(SMSText text: exampleTextThread) {
            System.out.println(text.getDate() + ":" + text.getBody());
        }
    }

}
