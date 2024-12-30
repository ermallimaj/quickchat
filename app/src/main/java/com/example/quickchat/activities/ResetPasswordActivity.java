package com.example.quickchat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.quickchat.R;
import com.example.quickchat.database.DatabaseHelper;
import com.example.quickchat.database.UserDao;
import com.example.quickchat.utils.InputValidator;
import com.example.quickchat.utils.PasswordHasher;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText etCode, etNewPassword;
    private MaterialButton btnSubmit;
    private String email, expectedCode;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etCode = findViewById(R.id.et_reset_code);
        etNewPassword = findViewById(R.id.et_new_password);
        btnSubmit = findViewById(R.id.btn_submit);

        email = getIntent().getStringExtra("email");
        expectedCode = getIntent().getStringExtra("code");

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        userDao = new UserDao(dbHelper.getReadableDatabase());

        btnSubmit.setOnClickListener(v -> {
            handlePasswordReset();
        });
    }

    private void handlePasswordReset() {
        String code = etCode.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString();

        if (!code.equals(expectedCode)) {
            etCode.setError("Incorrect code");
            return;
        }

        if (!InputValidator.isValidPassword(newPassword)) {
            etNewPassword.setError("Password must be at least 8 characters, include a number and a special character");
            return;
        }

        String hashedPassword = PasswordHasher.hashPassword(newPassword);

        boolean isPasswordUpdated = userDao.updateUserPassword(email, hashedPassword);

        if (isPasswordUpdated) {
            Toast.makeText(this, "Password reset successful", Toast.LENGTH_SHORT).show();
            navigateToLogin();
        } else {
            Toast.makeText(this, "Failed to reset password", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
