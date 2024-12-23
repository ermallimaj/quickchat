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

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText etCode, etNewPassword;
    private MaterialButton btnSubmit;
    private String email, expectedCode;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        dbHelper = new DatabaseHelper(this);

        etCode = findViewById(R.id.et_reset_code);
        etNewPassword = findViewById(R.id.et_new_password);
        btnSubmit = findViewById(R.id.btn_submit);

        email = getIntent().getStringExtra("email");
        expectedCode = getIntent().getStringExtra("code");

        btnSubmit.setOnClickListener(v -> {
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
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_PASSWORD, hashedPassword);

            int rows = db.update(DatabaseHelper.TABLE_USERS, values,
                    DatabaseHelper.COLUMN_EMAIL + "=?",
                    new String[]{email});

            if (rows > 0) {
                Toast.makeText(ResetPasswordActivity.this, "Password reset successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(ResetPasswordActivity.this, "Failed to reset password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
