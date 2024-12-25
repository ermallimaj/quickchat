package com.example.quickchat.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickchat.R;
import com.example.quickchat.adapters.ChatAdapter;
import com.example.quickchat.database.DatabaseHelper;
import com.example.quickchat.models.Chat;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private RecyclerView recyclerViewChats;
    private DatabaseHelper dbHelper;
    private String email, username;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvWelcome = findViewById(R.id.tv_welcome);
        recyclerViewChats = findViewById(R.id.recycler_view_chats);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        dbHelper = new DatabaseHelper(this);

        email = getIntent().getStringExtra("email");

        // Get username
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS,
                new String[]{DatabaseHelper.COLUMN_USERNAME},
                DatabaseHelper.COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            username = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME));
            cursor.close();
        }

        tvWelcome.setText("Welcome, " + username);

        // Set up RecyclerView
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(this));

        // Get recent chats
        List<Chat> chatList = dbHelper.getConversationsForUser(email);
        ChatAdapter chatAdapter = new ChatAdapter(chatList);
        recyclerViewChats.setAdapter(chatAdapter);

        // Handle Bottom Navigation item clicks
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_profile) {
                    Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                    profileIntent.putExtra("email", email);
                    startActivity(profileIntent);
                    return true;
                } else if (id == R.id.nav_new_chat) {
                    Intent chatIntent = new Intent(HomeActivity.this, NewChatActivity.class);
                    startActivity(chatIntent);
                    return true;
                } else if (id == R.id.nav_logout) {
                    Intent logoutIntent = new Intent(HomeActivity.this, LoginActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(logoutIntent);
                    finish();
                    return true;
                }

                return false;
            }
        });
    }

    // Helper method to format time ago
    public static String formatTimeAgo(String timestamp) {
        try {
            // Check if the timestamp is a valid long (milliseconds timestamp)
            if (timestamp.length() > 10) {  // If the length is greater than 10, assume it's in milliseconds (long format)
                long messageTimestamp = Long.parseLong(timestamp);
                return getTimeAgoFromMillis(messageTimestamp);
            } else {
                // Otherwise, it's a string in "yyyy-MM-dd HH:mm:ss" format
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
            return "Unknown time";  // In case of any error, return a fallback message
        }
    }

    // Helper method to format time from milliseconds
    private static String getTimeAgoFromMillis(long messageTimestamp) {
        long currentTime = System.currentTimeMillis();
        long diffInMillis = currentTime - messageTimestamp;

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
    }

}
