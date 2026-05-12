package com.example.todoapp.adapter.out.security;

import com.example.todoapp.domain.port.out.PasswordHasher;
import org.springframework.stereotype.Component;

@Component
public class Argon2PasswordHasher implements PasswordHasher {

    @Override
    public String hash(String raw) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public boolean matches(String raw, String hash) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
