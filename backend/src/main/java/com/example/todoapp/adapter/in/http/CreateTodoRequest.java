package com.example.todoapp.adapter.in.http;

import jakarta.validation.constraints.NotBlank;

public record CreateTodoRequest(@NotBlank(message = TITLE_BLANK_MESSAGE) String title) {

    static final String TITLE_BLANK_MESSAGE = "must not be blank";
}
