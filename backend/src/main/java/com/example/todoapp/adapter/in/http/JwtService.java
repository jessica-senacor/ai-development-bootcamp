package com.example.todoapp.adapter.in.http;

import com.example.todoapp.domain.model.AuthenticatedUser;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class JwtService {

    public String issue(AuthenticatedUser user) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    public UUID verify(String token) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
