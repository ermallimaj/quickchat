package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.utils.InputValidator;
import com.example.myapplication.utils.PasswordHasher;
import com.example.myapplication.utils.EmailSender;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;

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

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (!InputValidator.isValidEmail(email)) {
                etEmail.setError("Invalid email");
                return;
            }

            if (!InputValidator.isValidPassword(password)) {
                etPassword.setError("Password must be at least 8 characters, include a number and a special character");
                return;
            }

            String hashedPassword = PasswordHasher.hashPassword(password);

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(DatabaseHelper.TABLE_USERS,
                    null,
                    DatabaseHelper.COLUMN_EMAIL + "=? AND " + DatabaseHelper.COLUMN_PASSWORD + "=?",
                    new String[]{email, hashedPassword},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                // User exists, proceed to 2FA
                cursor.close();
                send2FACode(email);
            } else {
                Toast.makeText(LoginActivity.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
            }
        });

        btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });

        btnForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });
    }

    private void send2FACode(String email) {
        // Generate a 6-digit code
        int code = (int) (Math.random() * 900000) + 100000;
        generatedCode = String.valueOf(code);

        // Send email
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
