package com.example.todoapp.adapter.in.http;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateTodoRequest(
        @NotBlank(message = TITLE_BLANK_MESSAGE) String title,
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = DUE_DATE_FORMAT_MESSAGE) String dueDate) {

    static final String TITLE_BLANK_MESSAGE = "must not be blank";
    static final String DUE_DATE_FORMAT_MESSAGE = "must be a valid date in the format yyyy-MM-dd";
}
