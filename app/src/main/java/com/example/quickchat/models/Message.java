package com.example.quickchat.models;

import java.io.Serializable;

public class Message implements Serializable {
    private int messageId;
    private String text;
    private boolean isSent;
    private long timestamp;
    private int userId;
    private int receiverId;


    public Message(int messageId, String text, boolean isSent, long timestamp, int userId, int receiverId) {
        this.messageId = messageId;
        this.text = text;
        this.isSent = isSent;
        this.timestamp = timestamp;
        this.userId = userId;
        this.receiverId = receiverId;
    }

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
