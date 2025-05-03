package com.training.studyfx.model;

import java.time.LocalDateTime;
import java.util.List;

public class History {
    private LocalDateTime timestamp;
    private List<ChatMessage> chatMessages;

    public History(LocalDateTime timestamp, List<ChatMessage> chatMessages) {
        this.timestamp = timestamp;
        this.chatMessages = chatMessages;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    @Override
    public String toString() {
        // Customize how each history entry appears in the ListView.
        return timestamp.toString();
    }
}