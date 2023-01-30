package com.anma.skt.models;

import java.time.LocalDateTime;

public class Message {

    private String body;
    private LocalDateTime time;
    private String author;

    public Message() {
    }

    public Message(String body, LocalDateTime time, String author) {
        this.body = body;
        this.time = time;
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
