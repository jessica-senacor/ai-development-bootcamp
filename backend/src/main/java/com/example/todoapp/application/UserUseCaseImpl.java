package com.example.todoapp.application;

import com.example.todoapp.domain.model.User;
import com.example.todoapp.domain.port.in.UserUseCase;
import com.example.todoapp.domain.port.out.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserUseCaseImpl implements UserUseCase {

    private final UserRepository repository;

    public UserUseCaseImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User register(String username, String password) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public String login(String username, String password) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
