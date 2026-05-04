package com.example.todoapp.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "todo")
public class TodoJpaEntity {

    @Id
    private UUID id;
    private String title;
    private boolean completed;

    protected TodoJpaEntity() {}

    public TodoJpaEntity(UUID id, String title, boolean completed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public boolean isCompleted() { return completed; }
}
