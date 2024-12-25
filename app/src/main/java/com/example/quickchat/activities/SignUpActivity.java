package com.example.quickchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickchat.R;
import com.example.quickchat.database.DatabaseHelper;
import com.example.quickchat.database.UserDao;
import com.example.quickchat.models.User;
import com.example.quickchat.utils.InputValidator;
import com.example.quickchat.utils.PasswordHasher;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText etName, etSurname, etPhone, etUsername, etEmail, etPassword;
    private MaterialButton btnRegister;
    private ImageButton btnBack;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        userDao = new UserDao(dbHelper.getWritableDatabase());

        etName = findViewById(R.id.et_name_signup);
        etSurname = findViewById(R.id.et_surname_signup);
        etPhone = findViewById(R.id.et_phone_signup);
        etUsername = findViewById(R.id.et_username_signup);
        etEmail = findViewById(R.id.et_email_signup);
        etPassword = findViewById(R.id.et_password_signup);
        btnRegister = findViewById(R.id.btn_register);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String surname = etSurname.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (validateInputs(name, surname, username, email, password)) {
                String hashedPassword = PasswordHasher.hashPassword(password);

                User user = new User(0, username, email, name, surname, phone);
                user.setPassword(hashedPassword);

                long id = userDao.insertUser(user);
                if (id != -1) {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Registration failed. Email may already exist.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateInputs(String name, String surname, String username, String email, String password) {
        if (name.isEmpty()) {
            etName.setError("Name is required");
            return false;
        }

        if (surname.isEmpty()) {
            etSurname.setError("Surname is required");
            return false;
        }

        if (!InputValidator.isValidUsername(username)) {
            etUsername.setError("Username must be at least 3 characters");
            return false;
        }

        if (!InputValidator.isValidEmail(email)) {
            etEmail.setError("Invalid email");
            return false;
        }

        if (userDao.isEmailExists(email)) {
            etEmail.setError("Email already exists");
            return false;
        }

        if (!InputValidator.isValidPassword(password)) {
            etPassword.setError("Password must be at least 8 characters, include a number and a special character");
            return false;
        }
        return true;
    }
}
