package com.example.quickchat.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickchat.R;
import com.example.quickchat.adapters.UsersAdapter;
import com.example.quickchat.database.DatabaseHelper;
import com.example.quickchat.models.User;

import java.util.ArrayList;
import java.util.List;

public class NewChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUsers;
    private EditText etSearchUsers;
    private UsersAdapter userAdapter;
    private DatabaseHelper dbHelper;
    private List<User> userList;
    private List<User> filteredUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("NewChatActivity", "onCreate started");
        setContentView(R.layout.activity_new_chat);

        recyclerViewUsers = findViewById(R.id.recycler_view_users);
        etSearchUsers = findViewById(R.id.et_search_users);
        dbHelper = new DatabaseHelper(this);
        Log.d("NewChatActivity", "Views initialized");

        // Retrieve current user from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        int userId = preferences.getInt("userId", -1);
        String username = preferences.getString("username", "");
        String name = preferences.getString("name", "");
        String surname = preferences.getString("surname", "");
        String email = preferences.getString("email", "");
        String phone = preferences.getString("phone", "");

        if (userId == -1) {
            Log.e("NewChatActivity", "Current user is null");
            Toast.makeText(this, "Please log in again.", Toast.LENGTH_LONG).show();
            finish();  // Close activity if user is not logged in
            return;
        }

        User currentUser = new User(userId, username, email, name, surname, phone);

        // Retrieve user list from the database
        userList = dbHelper.getAllUsers();
        if (userList == null || userList.isEmpty()) {
            Log.d("NewChatActivity", "User list is empty or null");
            return; // Handle empty or null user list
        }
        Log.d("NewChatActivity", "User list size: " + userList.size());

        filteredUserList = new ArrayList<>(userList);

        // Remove current user from the list of users
        filteredUserList.removeIf(user -> user.getId() == currentUser.getId());
        Log.d("NewChatActivity", "Filtered user list size: " + filteredUserList.size());

        // Initialize the adapter and set it to the RecyclerView
        if (filteredUserList != null && !filteredUserList.isEmpty()) {
            userAdapter = new UsersAdapter(filteredUserList, user -> {
                Log.d("NewChatActivity", "User clicked: " + user.getUsername());
                Intent intent = new Intent(NewChatActivity.this, ChatActivity.class);
                intent.putExtra("currentUser", currentUser);
                intent.putExtra("chatUser", user);
                startActivity(intent);
            });

            recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewUsers.setAdapter(userAdapter);
        } else {
            Log.d("NewChatActivity", "No users available to display");
        }

        // Set up search functionality
        etSearchUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        Log.d("NewChatActivity", "onCreate finished");
    }

    // Method to filter users based on search query
    private void filterUsers(String query) {
        filteredUserList.clear();
        for (User user : userList) {
            if (user.getName().toLowerCase().contains(query.toLowerCase()) ||
                    user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                filteredUserList.add(user);
            }
        }
        if (userAdapter != null) {
            userAdapter.notifyDataSetChanged();  // Notify adapter that the list has changed
        }
        Log.d("NewChatActivity", "Filtered user list size after filter: " + filteredUserList.size());
    }
}
