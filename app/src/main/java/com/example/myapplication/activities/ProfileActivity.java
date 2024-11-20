package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.utils.InputValidator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etEmail;
    private MaterialButton btnUpdate;
    private DatabaseHelper dbHelper;
    private String email;
    private int userId;
    private String originalEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etUsername = findViewById(R.id.et_username_profile);
        etEmail = findViewById(R.id.et_email_profile);
        btnUpdate = findViewById(R.id.btn_update_profile);
        dbHelper = new DatabaseHelper(this);

        email = getIntent().getStringExtra("email");

        // Get user details
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS,
                null,
                DatabaseHelper.COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            String username = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME));
            originalEmail = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL));

            etUsername.setText(username);
            etEmail.setText(originalEmail);
            cursor.close();
        }

        btnUpdate.setOnClickListener(v -> {
            String newUsername = etUsername.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();

            if (!InputValidator.isValidUsername(newUsername)) {
                etUsername.setError("Username must be at least 3 characters");
                return;
            }

            if (!InputValidator.isValidEmail(newEmail)) {
                etEmail.setError("Invalid email");
                return;
            }

            SQLiteDatabase db1 = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USERNAME, newUsername);
            values.put(DatabaseHelper.COLUMN_EMAIL, newEmail);

            int rows = db1.update(DatabaseHelper.TABLE_USERS, values,
                    DatabaseHelper.COLUMN_ID + "=?",
                    new String[]{String.valueOf(userId)});

            if (rows > 0) {
                Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
