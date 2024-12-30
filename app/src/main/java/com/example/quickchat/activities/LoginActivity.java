package com.example.quickchat.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickchat.R;
import com.example.quickchat.database.DatabaseHelper;
import com.example.quickchat.database.UserDao;
import com.example.quickchat.models.User;
import com.example.quickchat.utils.EmailSender;
import com.example.quickchat.utils.InputValidator;
import com.example.quickchat.utils.PasswordHasher;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnSignUp, btnForgotPassword;
    private UserDao userDao;
    private String generatedCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        userDao = new UserDao(dbHelper.getReadableDatabase());

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.btn_sign_up);
        btnForgotPassword = findViewById(R.id.btn_forgot_password);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (!validateInputs(email, password)) return;

            String hashedPassword = PasswordHasher.hashPassword(password);

            if (userDao.validateUser(email, hashedPassword)) {
                saveCurrentUser(email);
                send2FACode(email);
            } else {
                Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
            }
        });

        btnSignUp.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
        btnForgotPassword.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }

    private boolean validateInputs(String email, String password) {
        if (!InputValidator.isValidEmail(email)) {
            etEmail.setError("Invalid email");
            return false;
        }
        if (!InputValidator.isValidPassword(password)) {
            etPassword.setError("Invalid password");
            return false;
        }
        return true;
    }

    private void saveCurrentUser(String email) {
        User user = userDao.getUserByEmail(email);
        if (user != null) {
            SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("userId", user.getId());
            editor.putString("username", user.getUsername());
            editor.putString("name", user.getName());
            editor.putString("surname", user.getSurname());
            editor.putString("email", user.getEmail());
            editor.putString("phone", user.getPhone());
            editor.apply();
        }
    }

    // Method to generate and send a 2FA code to the user's email
    private void send2FACode(String email) {
        int code = (int) (Math.random() * 900000) + 100000;
        generatedCode = String.valueOf(code);

        String subject = "QuickChat 2FA Code";
        String message = "Your 2FA code is: " + generatedCode;
        EmailSender.sendEmail(this, email, subject, message);

        Intent intent = new Intent(LoginActivity.this, TwoFactorAuthActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("code", generatedCode);
        startActivity(intent);
        finish();
    }
}
