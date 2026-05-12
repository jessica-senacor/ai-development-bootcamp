package com.example.todoapp.application;

import com.example.todoapp.domain.model.AuthenticatedUser;
import com.example.todoapp.domain.model.User;
import com.example.todoapp.domain.port.in.UserUseCase;
import com.example.todoapp.domain.port.out.PasswordHasher;
import com.example.todoapp.domain.port.out.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserUseCaseImpl implements UserUseCase {

    private final UserRepository repository;
    private final PasswordHasher passwordHasher;

    public UserUseCaseImpl(UserRepository repository, PasswordHasher passwordHasher) {
        this.repository = repository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public User register(String username, String password) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public AuthenticatedUser authenticate(String username, String password) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
