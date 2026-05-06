package com.example.todoapp.application;

import com.example.todoapp.domain.model.Todo;
import com.example.todoapp.domain.port.in.TodoUseCase;
import com.example.todoapp.domain.port.out.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TodoUseCaseImpl implements TodoUseCase {

    private final TodoRepository repository;

    public TodoUseCaseImpl(TodoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Todo> getAll() {
        return repository.findAll();
    }

    @Override
    public Todo create(String title) {
        return repository.save(new Todo(UUID.randomUUID(), title));
    }

    @Override
    public Todo toggle(UUID id) {
        Todo todo = repository.findById(id).orElseThrow();
        return repository.save(new Todo(todo.getId(), todo.getTitle(), !todo.isCompleted()));
    }
}
