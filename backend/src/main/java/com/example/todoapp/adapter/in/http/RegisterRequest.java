package com.example.todoapp.adapter.in.http;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String username,
        @NotBlank String password) {
}
