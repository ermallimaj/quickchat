package com.example.quickchat.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.quickchat.models.Chat;
import com.example.quickchat.models.Message;
import com.example.quickchat.models.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "secureapp.db";
    public static final int DATABASE_VERSION = 4;

    // Users table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SURNAME = "surname";
    public static final String COLUMN_PHONE = "phone";

    // Messages table
    public static final String TABLE_MESSAGES = "messages";
    public static final String COLUMN_MESSAGE_ID = "message_id";
    public static final String COLUMN_MESSAGE_TEXT = "message_text";
    public static final String COLUMN_IS_SENT = "is_sent";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_RECEIVER_ID = "receiver_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    // Create table SQL
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_USERNAME + " TEXT," +
            COLUMN_EMAIL + " TEXT UNIQUE," +
            COLUMN_PASSWORD + " TEXT," +
            COLUMN_NAME + " TEXT," +
            COLUMN_SURNAME + " TEXT," +
            COLUMN_PHONE + " TEXT" +
            ");";

    private static final String CREATE_TABLE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES + " (" +
            COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_MESSAGE_TEXT + " TEXT," +
            COLUMN_IS_SENT + " INTEGER," +
            COLUMN_USER_ID + " INTEGER," +
            COLUMN_RECEIVER_ID + " INTEGER," +
            COLUMN_TIMESTAMP + " INTEGER," +
            "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")," +
            "FOREIGN KEY (" + COLUMN_RECEIVER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")" +
            ");";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_MESSAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_NAME + " TEXT;");
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_SURNAME + " TEXT;");
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_PHONE + " TEXT;");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_MESSAGES + " ADD COLUMN " + COLUMN_TIMESTAMP + " INTEGER;");
        }
    }

    // Insert a new message
    public long insertMessage(String messageText, boolean isSent, int userId, int receiverId, long timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_MESSAGE_TEXT, messageText);
            values.put(COLUMN_IS_SENT, isSent ? 1 : 0);  // 1 for sent, 0 for received
            values.put(COLUMN_USER_ID, userId);
            values.put(COLUMN_RECEIVER_ID, receiverId);
            values.put(COLUMN_TIMESTAMP, timestamp);
            long messageId = db.insert(TABLE_MESSAGES, null, values);
            db.setTransactionSuccessful();
            return messageId;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // Retrieve messages between two users
    @SuppressLint("Range")
    public List<Message> getMessagesForChat(int userId, int receiverId) {
        List<Message> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_MESSAGES + " WHERE (" + COLUMN_USER_ID + " = ? AND " + COLUMN_RECEIVER_ID + " = ?) OR (" + COLUMN_USER_ID + " = ? AND " + COLUMN_RECEIVER_ID + " = ?) ORDER BY " + COLUMN_TIMESTAMP + " ASC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(receiverId), String.valueOf(receiverId), String.valueOf(userId)});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                messages.add(new Message(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_MESSAGE_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_TEXT)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_IS_SENT)) == 1,
                        cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_RECEIVER_ID))
                ));
            }
            cursor.close();
        }
        return messages;
    }

    // Check if an email exists in the database
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Retrieve recent chats for a user
    @SuppressLint("Range")
    public List<Chat> getRecentChatsForUser(String email) {
        List<Chat> chatList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT u.username, m." + COLUMN_MESSAGE_TEXT + ", m." + COLUMN_TIMESTAMP +
                " FROM " + TABLE_MESSAGES + " m " +
                "INNER JOIN " + TABLE_USERS + " u ON (m." + COLUMN_RECEIVER_ID + " = u." + COLUMN_ID +
                " OR m." + COLUMN_USER_ID + " = u." + COLUMN_ID + ")" +
                " WHERE m." + COLUMN_USER_ID + " = (SELECT " + COLUMN_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?)" +
                " ORDER BY m." + COLUMN_TIMESTAMP + " DESC LIMIT 10";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                chatList.add(new Chat(
                        cursor.getString(cursor.getColumnIndex("username")),
                        cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_TEXT)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP))
                ));
            }
            cursor.close();
        }
        return chatList;
    }

    // Retrieve all users
    @SuppressLint("Range")
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, null, null, null, null, COLUMN_USERNAME);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                users.add(new User(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_SURNAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_PHONE))
                ));
            }
            cursor.close();
        }
        return users;
    }

    // Delete old messages
    public void deleteOldMessages(long timestampCutoff) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MESSAGES, COLUMN_TIMESTAMP + " < ?", new String[]{String.valueOf(timestampCutoff)});
        db.close();
    }
}
