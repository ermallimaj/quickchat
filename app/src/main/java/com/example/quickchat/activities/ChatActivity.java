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
    private DatabaseHelper databaseHelper;
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

        // Get the current user and the user being chatted with
        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("currentUser");
        chatUser = (User) intent.getSerializableExtra("chatUser");

        if (currentUser == null || chatUser == null) {
            // Handle the error case where users are not passed properly
            Log.e("ChatActivity", "Current user or chat user is null");
            finish();  // Close the activity or show an error message
            return;
        }

        // Display the chat header with the username of the chat user
        tvChatHeader.setText("Chat with " + chatUser.getUsername());

        // Initialize database helper and RecyclerView adapter
        databaseHelper = new DatabaseHelper(this);
        messages = new ArrayList<>();

        // Load previous messages between users
        loadMessages();

        messageAdapter = new MessageAdapter(this, messages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        // Handle sending a new message
        btnSend.setOnClickListener(v -> {
            String messageText = etMessageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                // Get the current timestamp (in milliseconds)
                long timestamp = System.currentTimeMillis();

                // Insert the message into the database with the timestamp
                int messageId = (int) databaseHelper.insertMessage(messageText, true, currentUser.getId(), chatUser.getId(), timestamp);

                // Create a new Message object with the new messageId
                Message newMessage = new Message(messageId, messageText, true, timestamp, currentUser.getId(), chatUser.getId());

                // Add the message to the list and notify the adapter
                messages.add(newMessage);
                messageAdapter.notifyItemInserted(messages.size() - 1);
                recyclerViewMessages.scrollToPosition(messages.size() - 1);
                etMessageInput.setText("");
            }
        });
    }

    private void loadMessages() {
        // Fetch messages between the current user and the chat user
        List<Message> messageList = databaseHelper.getMessagesForChat(currentUser.getId(), chatUser.getId());
        messages.addAll(messageList);
    }
}
