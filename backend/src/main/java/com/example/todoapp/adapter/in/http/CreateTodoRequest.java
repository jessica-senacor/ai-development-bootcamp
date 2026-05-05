package com.example.todoapp.adapter.in.http;

import jakarta.validation.constraints.NotBlank;

public record CreateTodoRequest(@NotBlank String title) {}
