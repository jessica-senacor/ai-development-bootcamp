package com.example.todoapp.adapter.in.http;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for creating a new todo")
public record CreateTodoRequest(
        @Schema(description = "Title of the todo", example = "Buy groceries")
        @NotBlank(message = TITLE_BLANK_MESSAGE) String title,
        @Schema(description = "Optional due date in yyyy-MM-dd format", example = "2026-05-20", nullable = true)
        @ValidIsoDate String dueDate) {

    static final String TITLE_BLANK_MESSAGE = "must not be blank";
    static final String DUE_DATE_FORMAT_MESSAGE = "must be a valid date in the format yyyy-MM-dd";
}
