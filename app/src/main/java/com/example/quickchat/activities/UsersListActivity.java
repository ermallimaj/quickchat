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
import com.example.quickchat.adapters.UserAdapter;
import com.example.quickchat.database.DatabaseHelper;
import com.example.quickchat.database.UserDao;
import com.example.quickchat.models.User;

import java.util.ArrayList;
import java.util.List;

public class UsersListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUsers;
    private EditText etSearchUsers;
    private UserAdapter userAdapter;
    private UserDao userDao;
    private List<User> userList;
    private List<User> filteredUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("UsersListActivity", "onCreate started");
        setContentView(R.layout.activity_users_list);

        recyclerViewUsers = findViewById(R.id.recycler_view_users);
        etSearchUsers = findViewById(R.id.et_search_users);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        userDao = new UserDao(dbHelper.getReadableDatabase());
        Log.d("UsersListActivity", "UserDao initialized");

        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        int userId = preferences.getInt("userId", -1);

        if (userId == -1) {
            Log.e("UsersListActivity", "Current user is not logged in");
            Toast.makeText(this, "Please log in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        User currentUser = userDao.getUserById(userId);
        if (currentUser == null) {
            Log.e("UsersListActivity", "Current user not found in database");
            Toast.makeText(this, "User not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        userList = userDao.getAllUsers();
        if (userList == null || userList.isEmpty()) {
            Log.d("UsersListActivity", "User list is empty or null");
            return;
        }
        Log.d("UsersListActivity", "User list size: " + userList.size());

        filteredUserList = new ArrayList<>(userList);

        filteredUserList.removeIf(user -> user.getId() == currentUser.getId());
        Log.d("UsersListActivity", "Filtered user list size: " + filteredUserList.size());

        if (!filteredUserList.isEmpty()) {
            userAdapter = new UserAdapter(filteredUserList, user -> {
                Log.d("UsersListActivity", "User clicked: " + user.getUsername());

                Intent intent = new Intent(UsersListActivity.this, ChatActivity.class);
                intent.putExtra("currentUserId", currentUser.getId());
                intent.putExtra("chatUserId", user.getId());
                startActivity(intent);
            });

            recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewUsers.setAdapter(userAdapter);
        } else {
            Log.d("UsersListActivity", "No users available to display");
        }

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

        Log.d("UsersListActivity", "onCreate finished");
    }

    private void filterUsers(String query) {
        filteredUserList.clear();
        for (User user : userList) {
            if (user.getName().toLowerCase().contains(query.toLowerCase()) ||
                    user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                filteredUserList.add(user);
            }
        }
        if (userAdapter != null) {
            userAdapter.notifyDataSetChanged();
        }
        Log.d("UsersListActivity", "Filtered user list size after filter: " + filteredUserList.size());
    }
}
