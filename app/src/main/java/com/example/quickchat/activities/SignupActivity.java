package com.example.quickchat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import com.example.quickchat.R;
import com.example.quickchat.database.DatabaseHelper;
import com.example.quickchat.utils.InputValidator;
import com.example.quickchat.utils.PasswordHasher;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etEmail, etPassword;
    private MaterialButton btnRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        dbHelper = new DatabaseHelper(this);

        etUsername = findViewById(R.id.et_username_signup);
        etEmail = findViewById(R.id.et_email_signup);
        etPassword = findViewById(R.id.et_password_signup);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (!InputValidator.isValidUsername(username)) {
                etUsername.setError("Username must be at least 3 characters");
                return;
            }

            if (!InputValidator.isValidEmail(email)) {
                etEmail.setError("Invalid email");
                return;
            }

            if (!InputValidator.isValidPassword(password)) {
                etPassword.setError("Password must be at least 8 characters, include a number and a special character");
                return;
            }

            String hashedPassword = PasswordHasher.hashPassword(password);

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USERNAME, username);
            values.put(DatabaseHelper.COLUMN_EMAIL, email);
            values.put(DatabaseHelper.COLUMN_PASSWORD, hashedPassword);

            long id = db.insert(DatabaseHelper.TABLE_USERS, null, values);
            if (id != -1) {
                Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(SignUpActivity.this, "Registration failed. Email may already exist.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}