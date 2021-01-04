package com.example.thesis.DatabaseModels;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatRoom implements Serializable {

    private ArrayList<User> chatRoomUsers = new ArrayList<>();
    private String chatRoomId, chatRoomName, lastMessage, lastUserMessageId, lastMessageUserName;

    public ChatRoom(String chatRoomId, String chatRoomName, ArrayList<User> chatRoomUsers, String lastMessage, String lastUserMessageId,
                    String lastMessageUserName) {
        this.chatRoomId = chatRoomId;
        this.chatRoomName = chatRoomName;
        this.chatRoomUsers.addAll(chatRoomUsers);
        this.lastMessage = lastMessage;
        this.lastUserMessageId = lastUserMessageId;
        this.lastMessageUserName = lastMessageUserName;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public ArrayList<User> getChatRoomUsers() {
        return chatRoomUsers;
    }

    public void setChatRoomUsers(ArrayList<User> chatRoomUsers) {
        this.chatRoomUsers = chatRoomUsers;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastUserMessageId() {
        return lastUserMessageId;
    }

    public void setLastUserMessageId(String lastUserMessageId) {
        this.lastUserMessageId = lastUserMessageId;
    }

    public String getLastMessageUserName() {
        return lastMessageUserName;
    }

    public void setLastMessageUserName(String lastMessageUserName) {
        this.lastMessageUserName = lastMessageUserName;
    }
}
