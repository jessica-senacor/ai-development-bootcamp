package com.example.todoapp.adapter.in.http;

import com.example.todoapp.domain.model.AuthenticatedUser;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void issue_returnsNonEmptyToken() {
        AuthenticatedUser user = new AuthenticatedUser(UUID.randomUUID(), "alice");

        String token = jwtService.issue(user);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void verify_returnsUserIdFromIssuedToken() {
        UUID userId = UUID.randomUUID();
        AuthenticatedUser user = new AuthenticatedUser(userId, "alice");

        String token = jwtService.issue(user);

        assertEquals(userId, jwtService.verify(token));
    }

    @Test
    void verify_malformedToken_throws() {
        assertThrows(RuntimeException.class, () -> jwtService.verify("not-a-real-token"));
    }

    @Test
    void verify_emptyToken_throws() {
        assertThrows(RuntimeException.class, () -> jwtService.verify(""));
    }
}
