package com.example.quickchat.models;

import java.io.Serializable;

public class Message implements Serializable {
    private int messageId;         // Corresponds to message_id in the database
    private String text;           // Corresponds to message_text in the database
    private boolean isSent;        // Corresponds to is_sent in the database
    private long timestamp;        // Corresponds to timestamp in the database
    private int userId;            // Corresponds to user_id in the database
    private int receiverId;        // Corresponds to receiver_id in the database

    // Constructor with all columns, including message_id
    public Message(int messageId, String text, boolean isSent, long timestamp, int userId, int receiverId) {
        this.messageId = messageId;
        this.text = text;
        this.isSent = isSent;
        this.timestamp = timestamp;
        this.userId = userId;
        this.receiverId = receiverId;
    }

    // Getters and setters
    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }
}
