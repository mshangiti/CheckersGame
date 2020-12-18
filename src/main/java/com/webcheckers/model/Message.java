package com.webcheckers.model;

public class Message {
    private String text;
    private MessageType type;

    public Message (String msg, MessageType type) {
        this.text = msg;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
