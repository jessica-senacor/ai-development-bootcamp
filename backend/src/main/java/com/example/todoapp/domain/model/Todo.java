package com.example.todoapp.domain.model;

import java.util.UUID;

public class Todo {

    private final UUID id;
    private final String title;
    private final boolean completed;

    public Todo(UUID id, String title) {
        this(id, title, false);
    }

    public Todo(UUID id, String title, boolean completed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public boolean isCompleted() { return completed; }
}
