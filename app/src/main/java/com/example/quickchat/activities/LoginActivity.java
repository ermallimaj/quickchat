package com.example.quickchat.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickchat.R;
import com.example.quickchat.database.DatabaseHelper;
import com.example.quickchat.utils.EmailSender;
import com.example.quickchat.utils.InputValidator;
import com.example.quickchat.utils.PasswordHasher;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnSignUp, btnForgotPassword;
    private DatabaseHelper dbHelper;
    private String generatedCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.btn_sign_up);
        btnForgotPassword = findViewById(R.id.btn_forgot_password);

        // Login button click handler
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            // Validate email and password input
            if (!InputValidator.isValidEmail(email)) {
                etEmail.setError("Invalid email");
                return;
            }

            if (!InputValidator.isValidPassword(password)) {
                etPassword.setError("Password must be at least 8 characters, include a number and a special character");
                return;
            }

            // Hash the password before querying the database
            String hashedPassword = PasswordHasher.hashPassword(password);

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(DatabaseHelper.TABLE_USERS,
                    null,
                    DatabaseHelper.COLUMN_EMAIL + "=? AND " + DatabaseHelper.COLUMN_PASSWORD + "=?",
                    new String[]{email, hashedPassword},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                // User exists, proceed to 2FA
                @SuppressLint("Range") int userId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
                @SuppressLint("Range") String surname = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SURNAME));
                @SuppressLint("Range") String phone = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE));

                // Close the cursor
                cursor.close();

                // Save user information in SharedPreferences
                saveCurrentUser(userId, username, name, surname, email, phone);

                // Proceed to generate and send the 2FA code
                send2FACode(email);
            } else {
                // If the user does not exist or the credentials are incorrect
                Toast.makeText(LoginActivity.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
            }
        });

        // Sign up button click handler
        btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });

        // Forgot password button click handler
        btnForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });
    }

    // Method to save the current user's information to SharedPreferences
    private void saveCurrentUser(int userId, String username, String name, String surname, String email, String phone) {
        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("userId", userId);
        editor.putString("username", username);
        editor.putString("name", name);
        editor.putString("surname", surname);
        editor.putString("email", email);
        editor.putString("phone", phone);
        editor.apply();
    }

    // Method to generate and send a 2FA code to the user's email
    private void send2FACode(String email) {
        // Generate a 6-digit code
        int code = (int) (Math.random() * 900000) + 100000;
        generatedCode = String.valueOf(code);

        // Send email with the 2FA code
        String subject = "SecureApp 2FA Code";
        String message = "Your 2FA code is: " + generatedCode;
        EmailSender.sendEmail(this, email, subject, message);

        // Proceed to 2FA Activity
        Intent intent = new Intent(LoginActivity.this, TwoFactorAuthActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("code", generatedCode);
        startActivity(intent);
        finish();
    }
}
