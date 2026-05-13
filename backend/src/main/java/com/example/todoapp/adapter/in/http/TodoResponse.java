package com.example.todoapp.adapter.in.http;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "A todo item")
public record TodoResponse(
        @Schema(description = "Unique identifier") UUID id,
        @Schema(description = "Title of the todo", example = "Buy groceries") String title,
        @Schema(description = "Whether the todo is completed") boolean completed,
        @Schema(description = "Due date in yyyy-MM-dd format", example = "2026-05-20", nullable = true) String dueDate) {}
