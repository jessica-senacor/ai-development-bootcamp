package com.example.todoapp.adapter.in.http;

import java.util.UUID;

public record TodoResponse(UUID id, String title, boolean completed) {}
