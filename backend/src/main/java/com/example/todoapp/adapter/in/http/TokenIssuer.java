package com.example.todoapp.adapter.in.http;

import com.example.todoapp.domain.model.AuthenticatedUser;
import org.springframework.stereotype.Component;

@Component
public class TokenIssuer {

    public String issue(AuthenticatedUser user) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
