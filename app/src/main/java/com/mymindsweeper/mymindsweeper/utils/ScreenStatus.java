package com.mymindsweeper.mymindsweeper.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenStatus extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            System.out.println("Screen off");
        }
        else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            System.out.println("Screen on");
        }
    }
}