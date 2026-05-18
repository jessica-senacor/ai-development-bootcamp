package com.example.todoapp.adapter.in.http;

import com.example.todoapp.domain.model.AuthenticatedUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.UUID;

@Component
public class JwtService {

    private final SecretKey key = Jwts.SIG.HS256.key().build();

    public String issue(AuthenticatedUser user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .signWith(key)
                .compact();
    }

    public UUID verify(String token) {
        String subject = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return UUID.fromString(subject);
    }
}
