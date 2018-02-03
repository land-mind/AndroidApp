package com.mymindsweeper.mymindsweeper.sms;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class SMSUtils {

    public static final String INBOX = "content://sms/inbox";
    public static final String SENT = "content://sms/sent";
    public static final String DRAFT = "content://sms/draft";
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    //ask permission if read sms permission is not granted
    public static void initREAD_SMS(Activity activity) {
        if(ContextCompat.checkSelfPermission(activity.getBaseContext(), "android.permission.READ_SMS") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    //reads sms content and parses it and returns it as a list of SMSText
    public static List<SMSText> readSMS(Activity activity, String uri) {
        List<SMSText> messages = new ArrayList<>();
        SMSText.SMSType type = null;
        switch (uri) {
            case INBOX: type = SMSText.SMSType.INBOX; break;
            case SENT: type = SMSText.SMSType.SENT; break;
            case DRAFT: type = SMSText.SMSType.DRAFT; break;
        }

        if(ContextCompat.checkSelfPermission(activity.getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {
            Cursor cursor = activity.getContentResolver().query(Uri.parse(uri), null, null, null, null);
            boolean succeeded = cursor.moveToFirst();
            if (succeeded) {
                try {
                    messages.add(readSMSAtCursor(cursor, type));
                } catch (SMSException e) {
                    System.out.println(e);
                }
                while (cursor.moveToNext()) {
                    try {
                        messages.add(readSMSAtCursor(cursor, type));
                    } catch (SMSException e) {
                        System.out.println(e);
                    }
                }
            }
        }
        return messages;
    }

    private static SMSText readSMSAtCursor(Cursor cursor, SMSText.SMSType type) throws SMSException {
        int thread_id = -1;
        String phoneNumber = null;
        long date = -1;
        String body = null;
        boolean read = false;
        for(int i = 0; i < cursor.getColumnCount(); i++) {
            switch(cursor.getColumnName(i)) {
                case "thread_id": thread_id = Integer.parseInt(cursor.getString(i)); break;
                case "address": phoneNumber = cursor.getString(i); break;
                case "date": date = Long.parseLong(cursor.getString(i)); break;
                case "body": body = cursor.getString(i); break;
                case "read": read = cursor.getString(i).equals("1");
            }
        }

        if(phoneNumber == null || thread_id == -1 || body == null)
            throw new SMSException("Don't understand the sms format");

        return new SMSText(thread_id, phoneNumber, date, body, read, type);
    }
} 