package com.example.quickchat.activities;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quickchat.R;
import com.example.quickchat.adapters.ChatAdapter;
import com.example.quickchat.database.DatabaseHelper;
import com.example.quickchat.database.MessageDao;
import com.example.quickchat.database.UserDao;
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
    private String email, username;
    private UserDao userDao;
    private MessageDao messageDao;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvWelcome = findViewById(R.id.tv_welcome);
        recyclerViewChats = findViewById(R.id.recycler_view_chats);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        userDao = new UserDao(dbHelper.getReadableDatabase());
        messageDao = new MessageDao(dbHelper.getReadableDatabase());

        email = getIntent().getStringExtra("email");
        username = userDao.getUsernameByEmail(email);

        if (username != null) {
            tvWelcome.setText("Welcome, " + username);
        } else {
            tvWelcome.setText("Welcome!");
        }

        recyclerViewChats.setLayoutManager(new LinearLayoutManager(this));

        List<Chat> chatList = messageDao.getConversationsForUser(email);
        chatAdapter = new ChatAdapter(chatList);
        recyclerViewChats.setAdapter(chatAdapter);

        // ItemTouchHelper for swipe-to-delete
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                chatAdapter.removeItem(position);
            }

            public void onChildDraw(
                    @NonNull RecyclerView recyclerView,
                    @NonNull Canvas canvas,
                    @NonNull RecyclerView.ViewHolder viewHolder,
                    float dX,
                    float dY,
                    int actionState,
                    boolean isCurrentlyActive
            ) {
                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewChats);

        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
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
        });
    }
}
