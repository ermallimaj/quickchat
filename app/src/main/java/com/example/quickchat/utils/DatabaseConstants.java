package com.example.quickchat.utils;

public class DatabaseConstants {

    public static final String DATABASE_NAME = "secureapp.db";
    public static final int DATABASE_VERSION = 4;

    // Table Users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SURNAME = "surname";
    public static final String COLUMN_PHONE = "phone";

    public static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_USERNAME + " TEXT," +
            COLUMN_EMAIL + " TEXT UNIQUE," +
            COLUMN_PASSWORD + " TEXT," +
            COLUMN_NAME + " TEXT," +
            COLUMN_SURNAME + " TEXT," +
            COLUMN_PHONE + " TEXT" +
            ");";

    // Table Messages
    public static final String TABLE_MESSAGES = "messages";
    public static final String COLUMN_MESSAGE_ID = "message_id";
    public static final String COLUMN_MESSAGE_TEXT = "message_text";
    public static final String COLUMN_IS_SENT = "is_sent";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_RECEIVER_ID = "receiver_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    public static final String CREATE_TABLE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES + " (" +
            COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_MESSAGE_TEXT + " TEXT," +
            COLUMN_IS_SENT + " INTEGER," +
            COLUMN_USER_ID + " INTEGER," +
            COLUMN_RECEIVER_ID + " INTEGER," +
            COLUMN_TIMESTAMP + " INTEGER," +
            "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")," +
            "FOREIGN KEY (" + COLUMN_RECEIVER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")" +
            ");";
}
