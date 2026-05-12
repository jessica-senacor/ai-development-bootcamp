package com.example.todoapp.application;

import com.example.todoapp.domain.InvalidCredentialsException;
import com.example.todoapp.domain.UsernameAlreadyTakenException;
import com.example.todoapp.domain.model.User;
import com.example.todoapp.domain.port.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseImplTest {

    @Mock
    UserRepository repository;

    @InjectMocks
    UserUseCaseImpl userUseCase;

    @Test
    void register_savesUserWithHashedPassword() {
        when(repository.existsByUsername("alice")).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        userUseCase.register("alice", "secret");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(captor.capture());
        assertNotEquals("secret", captor.getValue().getPasswordHash());
    }

    @Test
    void register_returnsUserWithCorrectUsernameAndId() {
        when(repository.existsByUsername("alice")).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = userUseCase.register("alice", "secret");

        assertNotNull(result);
        assertEquals("alice", result.getUsername());
        assertNotNull(result.getId());
    }

    @Test
    void register_whenUsernameAlreadyExists_throwsException() {
        when(repository.existsByUsername("alice")).thenReturn(true);

        assertThrows(UsernameAlreadyTakenException.class, () -> userUseCase.register("alice", "secret"));
        verify(repository, never()).save(any());
    }

    @Test
    void login_withValidCredentials_returnsToken() {
        when(repository.existsByUsername("alice")).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        User registered = userUseCase.register("alice", "secret");
        when(repository.findByUsername("alice")).thenReturn(Optional.of(registered));

        String token = userUseCase.login("alice", "secret");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void login_withWrongPassword_throwsInvalidCredentialsException() {
        when(repository.existsByUsername("alice")).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        User registered = userUseCase.register("alice", "secret");
        when(repository.findByUsername("alice")).thenReturn(Optional.of(registered));

        assertThrows(InvalidCredentialsException.class, () -> userUseCase.login("alice", "wrong"));
    }

    @Test
    void login_withUnknownUsername_throwsInvalidCredentialsException() {
        when(repository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> userUseCase.login("ghost", "secret"));
    }
}
