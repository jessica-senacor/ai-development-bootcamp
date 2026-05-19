package com.example.todoapp.application;

import com.example.todoapp.domain.InvalidCredentialsException;
import com.example.todoapp.domain.UsernameAlreadyTakenException;
import com.example.todoapp.domain.model.AuthenticatedUser;
import com.example.todoapp.domain.model.User;
import com.example.todoapp.domain.port.in.UserUseCase;
import com.example.todoapp.domain.port.out.PasswordHasher;
import com.example.todoapp.domain.port.out.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
        if (repository.existsByUsername(username)) {
            throw new UsernameAlreadyTakenException();
        }
        return repository.save(new User(UUID.randomUUID(), username, passwordHasher.hash(password)));
    }

    @Override
    public AuthenticatedUser authenticate(String username, String password) {
        User user = repository.findByUsername(username)
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordHasher.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return new AuthenticatedUser(user.getId(), user.getUsername());
    }
}
