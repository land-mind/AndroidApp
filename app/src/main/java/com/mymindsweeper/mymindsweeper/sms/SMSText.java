package com.mymindsweeper.mymindsweeper.sms;

import java.util.ArrayList;
import java.util.List;

public class SMSText {

    public int thread_id;
    public String phoneNumber;
    public long date;
    public String body;
    public boolean read;
    private SMSType type;

    public enum SMSType {
        INBOX,
        SENT,
        DRAFT
    }

    public SMSText(String smsString, SMSType type) throws SMSException {
        this.type = type;
        thread_id = -1;
        String[] values = smsString.split(",");
        for(String v: values) {
            String keyvaluepair[] = v.split(":");
            switch(keyvaluepair[0]) {
                case "thread_id": thread_id = Integer.parseInt(keyvaluepair[1]); break;
                case "address": phoneNumber = keyvaluepair[1]; break;
                case "date": date = Long.parseLong(keyvaluepair[1]); break;
                case "body": body = keyvaluepair[1]; break;
                case "read": read = keyvaluepair[1].equals("1");
            }
        }
        if(phoneNumber == null || thread_id == -1)
            throw new SMSException("Don't understand the sms format");

    }

    public static List<SMSText> stringToArray(String string, SMSType type) {
        ArrayList<SMSText> smsTexts = new ArrayList<SMSText>();
        String[] lines = string.split("\n");
        for(String line: lines) {
            try {
                smsTexts.add(new SMSText(line, type));
            } catch(SMSException e) {
                System.out.println(e);
            }
        }
        return smsTexts;
    }
} 