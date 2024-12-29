package com.example.quickchat.activities;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Paint;

import com.example.quickchat.R;
import com.example.quickchat.adapters.ChatAdapter;
import com.example.quickchat.database.DatabaseHelper;
import com.example.quickchat.database.MessageDao;
import com.example.quickchat.database.UserDao;
import com.example.quickchat.models.Chat;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

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

        initViews();
        initDatabase();
        setupWelcomeMessage();
        setupRecyclerView();
        setupBottomNavigation();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        recyclerViewChats = findViewById(R.id.recycler_view_chats);
    }

    private void initDatabase() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        userDao = new UserDao(dbHelper.getReadableDatabase());
        messageDao = new MessageDao(dbHelper.getReadableDatabase());
        email = getIntent().getStringExtra("email");
        username = userDao.getUsernameByEmail(email);
    }

    private void setupWelcomeMessage() {
        if (username != null) {
            tvWelcome.setText("Welcome, " + username);
        } else {
            tvWelcome.setText("Welcome!");
        }
    }

    private void setupRecyclerView() {
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(this));
        List<Chat> chatList = messageDao.getConversationsForUser(email);
        chatAdapter = new ChatAdapter(chatList, chat -> {
            Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
            intent.putExtra("currentUserId", userDao.getUserIdByEmail(email));
            intent.putExtra("chatUserId", chat.getOtherUserId());
            startActivity(intent);
        });
        recyclerViewChats.setAdapter(chatAdapter);
        setupSwipeToDelete();
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                Chat chatToDelete = chatAdapter.getItem(position);

                int otherUserId = chatToDelete.getOtherUserId();

                messageDao.deleteMessagesForConversation(email, otherUserId);

                viewHolder.itemView.animate()
                        .translationX(swipeDir == ItemTouchHelper.LEFT ? -viewHolder.itemView.getWidth() : viewHolder.itemView.getWidth())
                        .alpha(0f)
                        .setDuration(300)
                        .withEndAction(() -> {
                            chatAdapter.removeItem(position);
                        })
                        .start();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                Paint paint = new Paint();
                if (dX < 0) {
                    paint.setColor(Color.parseColor("#FF4D4D"));
                } else {
                    paint.setColor(Color.TRANSPARENT);
                }
                c.drawRect(viewHolder.itemView.getLeft(), viewHolder.itemView.getTop(),
                        viewHolder.itemView.getRight(), viewHolder.itemView.getBottom(), paint);

                viewHolder.itemView.setTranslationX(dX);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        new ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(recyclerViewChats);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                profileIntent.putExtra("email", email);
                startActivity(profileIntent);
                return true;
            } else if (id == R.id.nav_new_chat) {
                Intent chatIntent = new Intent(HomeActivity.this, UsersListActivity.class);
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