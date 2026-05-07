package com.example.todoapp.domain.port.in;

import com.example.todoapp.domain.model.Todo;

import java.util.List;
import java.util.UUID;

public interface TodoUseCase {
    List<Todo> getAll();
    Todo create(String title, String dueDate);
    Todo toggle(UUID id);
    void delete(UUID id);
}
