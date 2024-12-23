package com.example.quickchat.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    // Helper method to format time ago
    public static String formatTimeAgo(String timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date messageDate = dateFormat.parse(timestamp);
            long diffInMillis = new Date().getTime() - messageDate.getTime();
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
            long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
            long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            if (diffInMinutes < 60) {
                return diffInMinutes + " minutes ago";
            } else if (diffInHours < 24) {
                return diffInHours + " hours ago";
            } else {
                return diffInDays + " days ago";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown time";
        }
    }
}
