package com.mymindsweeper.mymindsweeper.sms;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

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

    //reads inbox and returns it as csv format
    public static String readSMS(Activity activity, String uri) {
        StringBuilder allMsgs = new StringBuilder();
        if(ContextCompat.checkSelfPermission(activity.getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {
            Cursor cursor = activity.getContentResolver().query(Uri.parse(uri), null, null, null, null);
            boolean succeeded = cursor.moveToFirst();
            if (succeeded) {
                allMsgs.append(readSMSAtCursor(cursor) + '\n');
                while (cursor.moveToNext()) {
                    allMsgs.append(readSMSAtCursor(cursor) + '\n');
                }
            }
        }
        return allMsgs.toString();
    }

    private static String readSMSAtCursor(Cursor cursor) {
        StringBuilder msg = new StringBuilder();
        for(int i = 0; i < cursor.getColumnCount(); i++) {
            msg.append(cursor.getColumnName(i) + ":" + cursor.getString(i) + ',');
        }
        return msg.toString();
    }
} 