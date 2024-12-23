package com.example.quickchat.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.quickchat.utils.NotificationUtils;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Show notification
        NotificationUtils.showNotification(context, "Alarm", "This is an alarm notification.");
    }
}
