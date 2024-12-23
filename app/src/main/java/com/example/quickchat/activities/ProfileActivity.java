package com.example.quickchat.activities;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickchat.R;
import com.example.quickchat.database.DatabaseHelper;
import com.example.quickchat.utils.InputValidator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etEmail, etName, etSurname, etPhone;
    private MaterialButton btnUpdate;
    private DatabaseHelper dbHelper;
    private String email;
    private int userId;
    private String originalEmail;
    private ImageButton btnBack;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etUsername = findViewById(R.id.et_username_profile);
        etEmail = findViewById(R.id.et_email_profile);
        etName = findViewById(R.id.et_name_profile);
        etSurname = findViewById(R.id.et_surname_profile);
        etPhone = findViewById(R.id.et_phone_profile);
        btnUpdate = findViewById(R.id.btn_update_profile);
        dbHelper = new DatabaseHelper(this);

        email = getIntent().getStringExtra("email");

        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> {
            finish();
        });


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
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            String surname = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SURNAME));
            String phone = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE));

            etUsername.setText(username);
            etEmail.setText(originalEmail);
            etName.setText(name);
            etSurname.setText(surname);
            etPhone.setText(phone);
            cursor.close();
        }

        btnUpdate.setOnClickListener(v -> {
            String newUsername = etUsername.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();
            String newName = etName.getText().toString().trim();
            String newSurname = etSurname.getText().toString().trim();
            String newPhone = etPhone.getText().toString().trim();

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
            values.put(DatabaseHelper.COLUMN_NAME, newName);
            values.put(DatabaseHelper.COLUMN_SURNAME, newSurname);
            values.put(DatabaseHelper.COLUMN_PHONE, newPhone);

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