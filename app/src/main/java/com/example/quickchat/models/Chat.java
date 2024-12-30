package com.example.quickchat.models;

import java.io.Serializable;

public class Chat implements Serializable {

    private String username;
    private int otherUserId;
    private String lastMessage;
    private String timestamp;

    public Chat(String otherUsername, String lastMessage, String timestamp, int otherUserId) {
        this.username = otherUsername;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.otherUserId = otherUserId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getOtherUserId() {
        return otherUserId;
    }

    public String getUsername() {
        return username;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
