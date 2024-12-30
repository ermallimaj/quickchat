package com.example.quickchat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.quickchat.utils.DatabaseConstants;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(@Nullable Context context) {
        super(context, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseConstants.CREATE_TABLE_USERS);
        db.execSQL(DatabaseConstants.CREATE_TABLE_MESSAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + DatabaseConstants.TABLE_USERS + " ADD COLUMN name TEXT;");
            db.execSQL("ALTER TABLE " + DatabaseConstants.TABLE_USERS + " ADD COLUMN surname TEXT;");
            db.execSQL("ALTER TABLE " + DatabaseConstants.TABLE_USERS + " ADD COLUMN phone TEXT;");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + DatabaseConstants.TABLE_MESSAGES + " ADD COLUMN timestamp INTEGER;");
        }
    }
}
