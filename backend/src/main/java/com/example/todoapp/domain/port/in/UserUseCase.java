package com.example.todoapp.domain.port.in;

import com.example.todoapp.domain.model.User;

public interface UserUseCase {

    User register(String username, String password);

    String login(String username, String password);
}
