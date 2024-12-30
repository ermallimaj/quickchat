package com.example.quickchat.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickchat.R;
import com.example.quickchat.database.DatabaseHelper;
import com.example.quickchat.database.UserDao;
import com.example.quickchat.models.User;
import com.example.quickchat.utils.InputValidator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etEmail, etName, etSurname, etPhone;
    private MaterialButton btnUpdate;
    private UserDao userDao;
    private int userId;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etUsername = findViewById(R.id.et_username_profile);
        etEmail = findViewById(R.id.et_email_profile);
        etName = findViewById(R.id.et_name_profile);
        etSurname = findViewById(R.id.et_surname_profile);
        etPhone = findViewById(R.id.et_phone_profile);
        btnUpdate = findViewById(R.id.btn_update_profile);
        btnBack = findViewById(R.id.btn_back);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        userDao = new UserDao(dbHelper.getReadableDatabase());

        String email = getIntent().getStringExtra("email");

        btnBack.setOnClickListener(v -> finish());

        User user = userDao.getUserByEmail(email);
        if (user != null) {
            userId = user.getId();
            etUsername.setText(user.getUsername());
            etEmail.setText(user.getEmail());
            etName.setText(user.getName());
            etSurname.setText(user.getSurname());
            etPhone.setText(user.getPhone());
        } else {
            Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnUpdate.setOnClickListener(v -> {
            String newUsername = etUsername.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();
            String newName = etName.getText().toString().trim();
            String newSurname = etSurname.getText().toString().trim();
            String newPhone = etPhone.getText().toString().trim();

            if (!InputValidator.isValidUsername(newUsername)) {
                etUsername.setError("Username must be at least 3 characters");
                return;
            }
            if (!InputValidator.isValidEmail(newEmail)) {
                etEmail.setError("Invalid email");
                return;
            }

            User updatedUser = new User(userId, newUsername, newEmail, newName, newSurname, newPhone);
            boolean isUpdated = userDao.updateUser(updatedUser);

            if (isUpdated) {
                Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}