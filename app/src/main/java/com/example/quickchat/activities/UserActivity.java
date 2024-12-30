package com.example.quickchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

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

public class UserActivity extends AppCompatActivity {
    private EditText searchEditText;
    private RecyclerView usersRecyclerView;
    private UserAdapter userAdapter;
    private List<User> usersList;
    private List<User> filteredList;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        searchEditText = findViewById(R.id.et_search_users);
        usersRecyclerView = findViewById(R.id.recycler_view_users);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        userDao = new UserDao(dbHelper.getWritableDatabase());
        usersList = userDao.getAllUsers();
        filteredList = new ArrayList<>(usersList);

            userAdapter = new UserAdapter(filteredList, user -> {
            Intent intent = new Intent(UserActivity.this, ChatActivity.class);
            intent.putExtra("user_id", user.getId());
            startActivity(intent);
        });

        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.setAdapter(userAdapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterUsers(String query) {
        filteredList.clear();
        for (User user : usersList) {
            if (user.getName().toLowerCase().contains(query.toLowerCase()) ||
                    user.getSurname().toLowerCase().contains(query.toLowerCase()) ||
                    user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }
        userAdapter.updateList(filteredList);
    }
}