package com.example.quickchat.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.quickchat.models.Chat;
import com.example.quickchat.models.Message;
import com.example.quickchat.utils.DatabaseConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MessageDao {

    private final SQLiteDatabase db;

    public MessageDao(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertMessage(String messageText, boolean isSent, int userId, int receiverId, long timestamp) {
        ContentValues values = new ContentValues();
        values.put("message_text", messageText);
        values.put("is_sent", isSent ? 1 : 0);
        values.put("user_id", userId);
        values.put("receiver_id", receiverId);
        values.put("timestamp", timestamp);
        return db.insert("messages", null, values);
    }

    @SuppressLint("Range")
    public List<Message> getMessagesForChat(int userId, int receiverId) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM messages WHERE (user_id = ? AND receiver_id = ?) " +
                "OR (user_id = ? AND receiver_id = ?) ORDER BY timestamp ASC";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(userId), String.valueOf(receiverId),
                String.valueOf(receiverId), String.valueOf(userId)
        });

        if (cursor != null) {
            while (cursor.moveToNext()) {
                messages.add(new Message(
                        cursor.getInt(cursor.getColumnIndex("message_id")),
                        cursor.getString(cursor.getColumnIndex("message_text")),
                        cursor.getInt(cursor.getColumnIndex("is_sent")) == 1,
                        cursor.getLong(cursor.getColumnIndex("timestamp")),
                        cursor.getInt(cursor.getColumnIndex("user_id")),
                        cursor.getInt(cursor.getColumnIndex("receiver_id"))
                ));
            }
            cursor.close();
        }
        return messages;
    }

    @SuppressLint("Range")
    public List<Chat> getConversationsForUser(String email) {
        List<Chat> chatList = new ArrayList<>();

        String query = "SELECT m." + DatabaseConstants.COLUMN_MESSAGE_TEXT + ", " +
                "m." + DatabaseConstants.COLUMN_TIMESTAMP + ", " +
                "m." + DatabaseConstants.COLUMN_USER_ID + ", " +
                "m." + DatabaseConstants.COLUMN_RECEIVER_ID + ", " +
                "u1." + DatabaseConstants.COLUMN_USERNAME + " AS senderUsername, " +
                "u2." + DatabaseConstants.COLUMN_USERNAME + " AS receiverUsername " +
                "FROM " + DatabaseConstants.TABLE_MESSAGES + " AS m " +
                "INNER JOIN " + DatabaseConstants.TABLE_USERS + " AS u1 " +
                "ON m." + DatabaseConstants.COLUMN_USER_ID + " = u1." + DatabaseConstants.COLUMN_ID + " " +
                "INNER JOIN " + DatabaseConstants.TABLE_USERS + " AS u2 " +
                "ON m." + DatabaseConstants.COLUMN_RECEIVER_ID + " = u2." + DatabaseConstants.COLUMN_ID + " " +
                "WHERE m." + DatabaseConstants.COLUMN_USER_ID + " = (" +
                "SELECT " + DatabaseConstants.COLUMN_ID + " " +
                "FROM " + DatabaseConstants.TABLE_USERS + " " +
                "WHERE " + DatabaseConstants.COLUMN_EMAIL + " = ?" +
                ") " +
                "OR m." + DatabaseConstants.COLUMN_RECEIVER_ID + " = (" +
                "SELECT " + DatabaseConstants.COLUMN_ID + " " +
                "FROM " + DatabaseConstants.TABLE_USERS + " " +
                "WHERE " + DatabaseConstants.COLUMN_EMAIL + " = ?" +
                ") " +
                "ORDER BY m." + DatabaseConstants.COLUMN_TIMESTAMP + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{email, email});

        if (cursor != null) {
            Map<String, Chat> conversations = new HashMap<>();

            while (cursor.moveToNext()) {
                String message = cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_MESSAGE_TEXT));
                String timestamp = cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_TIMESTAMP));
                String senderId = cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_USER_ID));
                String receiverId = cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_RECEIVER_ID));
                String senderUsername = cursor.getString(cursor.getColumnIndex("senderUsername"));
                String receiverUsername = cursor.getString(cursor.getColumnIndex("receiverUsername"));

                String loggedInUserId = UserDao.getUserIdByEmail(email, db);

                String otherUsername = senderId.equals(loggedInUserId) ? receiverUsername : senderUsername;

                String conversationKey = senderId.compareTo(receiverId) < 0 ?
                        senderId + "_" + receiverId : receiverId + "_" + senderId;

                if (!conversations.containsKey(conversationKey)) {
                    conversations.put(conversationKey, new Chat(otherUsername, message, timestamp));
                } else {
                    Chat currentChat = conversations.get(conversationKey);
                    currentChat.setLastMessage(message);
                    currentChat.setTimestamp(timestamp);
                }
            }

            chatList.addAll(conversations.values());
            cursor.close();
        }

        return chatList;
    }

    public void deleteMessagesForConversation(int userId, int receiverId) {
        String whereClause = "(user_id = ? AND receiver_id = ?) OR (user_id = ? AND receiver_id = ?)";
        String[] whereArgs = new String[]{String.valueOf(userId), String.valueOf(receiverId),
                String.valueOf(receiverId), String.valueOf(userId)};
        db.delete("messages", whereClause, whereArgs);
    }
}