package com.example.todoapp.domain.port.out;

import com.example.todoapp.domain.model.Todo;

import java.util.List;

public interface TodoRepository {
    Todo save(Todo todo);
    List<Todo> findAll();
    void deleteAll();
}
