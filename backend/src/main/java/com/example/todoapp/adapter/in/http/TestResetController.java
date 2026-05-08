package com.example.todoapp.adapter.in.http;

import com.example.todoapp.domain.port.out.TodoRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("test")
@RestController
@RequestMapping("/api/todos")
public class TestResetController {

    private final TodoRepository repository;

    public TestResetController(TodoRepository repository) {
        this.repository = repository;
    }

    @DeleteMapping("/reset")
    public void reset() {
        repository.deleteAll();
        // TODO: also call userRepository.deleteAll() once UserRepository port exists
    }
}
