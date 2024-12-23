package com.example.quickchat.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickchat.R;
import com.example.quickchat.database.DatabaseHelper;
import com.example.quickchat.utils.InputValidator;
import com.example.quickchat.utils.PasswordHasher;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText etName, etSurname, etPhone, etUsername, etEmail, etPassword;
    private MaterialButton btnRegister;
    private ImageButton btnBack;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.et_name_signup);
        etSurname = findViewById(R.id.et_surname_signup);
        etPhone = findViewById(R.id.et_phone_signup);
        etUsername = findViewById(R.id.et_username_signup);
        etEmail = findViewById(R.id.et_email_signup);
        etPassword = findViewById(R.id.et_password_signup);
        btnRegister = findViewById(R.id.btn_register);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String surname = etSurname.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            // Validate inputs
            if (name.isEmpty()) {
                etName.setError("Name is required");
                return;
            }

            if (surname.isEmpty()) {
                etSurname.setError("Surname is required");
                return;
            }

            if (!InputValidator.isValidUsername(username)) {
                etUsername.setError("Username must be at least 3 characters");
                return;
            }

            if (!InputValidator.isValidEmail(email)) {
                etEmail.setError("Invalid email");
                return;
            }

            if (dbHelper.isEmailExists(email)) {
                etEmail.setError("Email already exists");
                return;
            }

            if (!InputValidator.isValidPassword(password)) {
                etPassword.setError("Password must be at least 8 characters, include a number and a special character");
                return;
            }

            String hashedPassword = PasswordHasher.hashPassword(password);

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_NAME, name);
            values.put(DatabaseHelper.COLUMN_SURNAME, surname);
            values.put(DatabaseHelper.COLUMN_PHONE, phone);
            values.put(DatabaseHelper.COLUMN_USERNAME, username);
            values.put(DatabaseHelper.COLUMN_EMAIL, email);
            values.put(DatabaseHelper.COLUMN_PASSWORD, hashedPassword);

            long id = db.insert(DatabaseHelper.TABLE_USERS, null, values);
            if (id != -1) {
                Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            } else {
                Log.d("SignUpActivity", "Email being checked: " + email);
                Toast.makeText(SignUpActivity.this, "Registration failed. Email may already exist.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}