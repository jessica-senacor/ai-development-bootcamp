package com.example.todoapp.domain.port.in;

import com.example.todoapp.domain.model.Todo;

import java.util.List;

public interface TodoUseCase {
    List<Todo> getAll();
    Todo create(String title);
}
