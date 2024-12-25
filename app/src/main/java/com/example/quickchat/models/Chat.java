package com.example.quickchat.models;

import java.io.Serializable;

public class Chat implements Serializable {

    private String username;
    private String lastMessage;
    private String timestamp;

    public Chat(String username, String lastMessage, String timestamp) {
        this.username = username;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
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
