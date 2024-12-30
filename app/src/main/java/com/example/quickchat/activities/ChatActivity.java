package com.example.quickchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickchat.R;
import com.example.quickchat.adapters.MessageAdapter;
import com.example.quickchat.database.DatabaseHelper;
import com.example.quickchat.database.MessageDao;
import com.example.quickchat.database.UserDao;
import com.example.quickchat.models.Message;
import com.example.quickchat.models.User;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private TextView tvChatHeader;
    private RecyclerView recyclerViewMessages;
    private EditText etMessageInput;
    private ImageButton btnSend;

    private ArrayList<Message> messages;
    private MessageAdapter messageAdapter;
    private MessageDao messageDao;
    private UserDao userDao;
    private User currentUser;
    private User chatUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvChatHeader = findViewById(R.id.tv_chat_header);
        recyclerViewMessages = findViewById(R.id.recycler_view_messages);
        etMessageInput = findViewById(R.id.et_message_input);
        btnSend = findViewById(R.id.btn_send);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        messageDao = new MessageDao(databaseHelper.getWritableDatabase());
        userDao = new UserDao(databaseHelper.getReadableDatabase());

        Intent intent = getIntent();
        int currentUserId = intent.getIntExtra("currentUserId", -1);
        int chatUserId = intent.getIntExtra("chatUserId", -1);

        if (currentUserId == -1 || chatUserId == -1) {
            Log.e("ChatActivity", "Invalid user IDs passed via Intent");
            finish();
            return;
        }

        currentUser = userDao.getUserById(currentUserId);
        chatUser = userDao.getUserById(chatUserId);

        if (currentUser == null || chatUser == null) {
            Log.e("ChatActivity", "User data not found in the database");
            finish();
            return;
        }

        tvChatHeader.setText("Chat with " + chatUser.getUsername());

        messages = new ArrayList<>();

        loadMessages();

        messageAdapter = new MessageAdapter(this, messages);

        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        btnSend.setOnClickListener(v -> {
            String messageText = etMessageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
            }
        });
    }

    private void loadMessages() {
        List<Message> messageList = messageDao.getMessagesForChat(currentUser.getId(), chatUser.getId());

        messages.addAll(messageList);
    }

    private void sendMessage(String messageText) {
        long timestamp = System.currentTimeMillis();

        long messageId = messageDao.insertMessage(
                messageText,
                true,
                currentUser.getId(),
                chatUser.getId(),
                timestamp
        );

        if (messageId != -1) {
            Message newMessage = new Message((int) messageId, messageText, true, timestamp, currentUser.getId(), chatUser.getId());
            messages.add(newMessage);

            messageAdapter.notifyItemInserted(messages.size() - 1);
            recyclerViewMessages.scrollToPosition(messages.size() - 1);

            etMessageInput.setText("");
        } else {
            Log.e("ChatActivity", "Failed to insert message into the database");
        }
    }
}
