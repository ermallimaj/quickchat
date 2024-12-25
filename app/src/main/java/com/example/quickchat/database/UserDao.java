package com.example.quickchat.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.quickchat.models.User;
import com.example.quickchat.utils.DatabaseConstants;

import java.util.ArrayList;
import java.util.List;

public class UserDao {

    private final SQLiteDatabase db;

    public UserDao(SQLiteDatabase db) {
        this.db = db;
    }

    public boolean isEmailExists(String email) {
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    @SuppressLint("Range")
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        Cursor cursor = db.query("users", null, null, null, null, null, "username");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                users.add(new User(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("username")),
                        cursor.getString(cursor.getColumnIndex("email")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("surname")),
                        cursor.getString(cursor.getColumnIndex("phone"))
                ));
            }
            cursor.close();
        }
        return users;
    }

    public boolean validateUser(String email, String hashedPassword) {
        Cursor cursor = db.query(
                DatabaseConstants.TABLE_USERS,
                null,
                DatabaseConstants.COLUMN_EMAIL + "=? AND " + DatabaseConstants.COLUMN_PASSWORD + "=?",
                new String[]{email, hashedPassword},
                null, null, null
        );

        boolean isValid = cursor != null && cursor.moveToFirst();
        if (cursor != null) {
            cursor.close();
        }
        return isValid;
    }

    public User getUserByEmail(String email) {
        Cursor cursor = db.query(
                DatabaseConstants.TABLE_USERS,
                null,
                DatabaseConstants.COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") User user = new User(
                    cursor.getInt(cursor.getColumnIndex(DatabaseConstants.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_USERNAME)),
                    cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_SURNAME)),
                    cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_PHONE))
            );
            cursor.close();
            return user;
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public User getUserById(int userId) {
        User user = null;
        Cursor cursor = null;

        try {
            String query = "SELECT id, username, email, name, surname, phone FROM users WHERE id = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String surname = cursor.getString(cursor.getColumnIndexOrThrow("surname"));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));

                user = new User(id, username, email, name, surname, phone);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return user;
    }

    public long insertUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_NAME, user.getName());
        values.put(DatabaseConstants.COLUMN_SURNAME, user.getSurname());
        values.put(DatabaseConstants.COLUMN_PHONE, user.getPhone());
        values.put(DatabaseConstants.COLUMN_USERNAME, user.getUsername());
        values.put(DatabaseConstants.COLUMN_EMAIL, user.getEmail());
        values.put(DatabaseConstants.COLUMN_PASSWORD, user.getPassword());

        return db.insert(DatabaseConstants.TABLE_USERS, null, values);
    }

    @SuppressLint("Range")
    public static String getUserIdByEmail(String email, SQLiteDatabase db) {
        String userId = null;
        Cursor cursor = null;
        try {
            String query = "SELECT " + DatabaseConstants.COLUMN_ID + " FROM " + DatabaseConstants.TABLE_USERS +
                    " WHERE " + DatabaseConstants.COLUMN_EMAIL + " = ?";
            cursor = db.rawQuery(query, new String[]{email});
            if (cursor != null && cursor.moveToFirst()) {
                userId = cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_ID));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return userId;
    }

    @SuppressLint("Range")
    public String getUsernameByEmail(String email) {
        String username = null;
        Cursor cursor = null;

        try {
            cursor = db.query(DatabaseConstants.TABLE_USERS,
                    new String[]{DatabaseConstants.COLUMN_USERNAME},
                    DatabaseConstants.COLUMN_EMAIL + " = ?",
                    new String[]{email},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                username = cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_USERNAME));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return username;
    }

    public boolean updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_USERNAME, user.getUsername());
        values.put(DatabaseConstants.COLUMN_EMAIL, user.getEmail());
        values.put(DatabaseConstants.COLUMN_NAME, user.getName());
        values.put(DatabaseConstants.COLUMN_SURNAME, user.getSurname());
        values.put(DatabaseConstants.COLUMN_PHONE, user.getPhone());

        int rows = db.update(
                DatabaseConstants.TABLE_USERS,
                values,
                DatabaseConstants.COLUMN_ID + " = ?",
                new String[]{String.valueOf(user.getId())}
        );

        return rows > 0;
    }

    public boolean updateUserPassword(String email, String newPassword) {
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_PASSWORD, newPassword);

        int rows = db.update(DatabaseConstants.TABLE_USERS, values,
                DatabaseConstants.COLUMN_EMAIL + "=?",
                new String[]{email});

        return rows > 0;
    }
}
