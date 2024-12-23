package com.example.quickchat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import com.example.quickchat.R;
import com.example.quickchat.database.DatabaseHelper;
import com.example.quickchat.utils.EmailSender;
import com.example.quickchat.utils.InputValidator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private MaterialButton btnResetPassword;
    private DatabaseHelper dbHelper;
    private String resetCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        dbHelper = new DatabaseHelper(this);

        etEmail = findViewById(R.id.et_email_reset);
        btnResetPassword = findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (!InputValidator.isValidEmail(email)) {
                etEmail.setError("Invalid email");
                return;
            }

            // Check if email exists
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(DatabaseHelper.TABLE_USERS,
                    null,
                    DatabaseHelper.COLUMN_EMAIL + "=?",
                    new String[]{email},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                cursor.close();
                sendResetCode(email);
            } else {
                Toast.makeText(ForgotPasswordActivity.this, "Email not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendResetCode(String email) {
        // Generate a 6-digit code
        int code = (int) (Math.random() * 900000) + 100000;
        resetCode = String.valueOf(code);

        // Send email
        String subject = "SecureApp Password Reset";
        String message = "Your password reset code is: " + resetCode;
        EmailSender.sendEmail(this, email, subject, message);

        // Proceed to Reset Password Activity
        Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("code", resetCode);
        startActivity(intent);
        finish();
    }
}
