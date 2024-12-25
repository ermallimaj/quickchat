package com.example.quickchat.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static String formatTimeAgo(String timestamp) {
        try {
            if (timestamp.length() > 10) {
                long messageTimestamp = Long.parseLong(timestamp);
                return getTimeAgoFromMillis(messageTimestamp);
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date messageDate = dateFormat.parse(timestamp);
                if (messageDate != null) {
                    return getTimeAgoFromMillis(messageDate.getTime());
                } else {
                    return "Unknown time";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown time";
        }
    }

    private static String getTimeAgoFromMillis(long messageTimestamp) {
        long currentTime = System.currentTimeMillis();
        long diffInMillis = currentTime - messageTimestamp;

        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
        long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

        if (diffInMinutes < 60) {
            return diffInMinutes + " minute" + (diffInMinutes != 1 ? "s" : "") + " ago";
        } else if (diffInHours < 24) {
            return diffInHours + " hour" + (diffInHours != 1 ? "s" : "") + " ago";
        } else {
            return diffInDays + " day" + (diffInDays != 1 ? "s" : "") + " ago";
        }
    }
}
