package com.mymindsweeper.mymindsweeper.sms;

public class SMSText {

    private int thread_id;
    private String phoneNumber;
    private long date;
    private String body;
    private boolean read;
    private SMSType type;

    public enum SMSType {
        INBOX,
        SENT,
        DRAFT
    }

    public SMSText(int thread_id, String phoneNumber, long date, String body, boolean read, SMSType type) {
        this.type = type;
        this.thread_id = thread_id;
        this.phoneNumber = phoneNumber;
        this.date = date;
        this.body = body;
        this.read = read;
    }

    public int getThreadId() {
        return thread_id;
    }

    public String getBody() {
        return body;
    }

    public long getDate() {
        return date;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
} 