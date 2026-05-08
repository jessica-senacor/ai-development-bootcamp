package com.example.todoapp.adapter.in.http;

import com.example.todoapp.domain.InvalidCredentialsException;
import com.example.todoapp.domain.UsernameAlreadyTakenException;
import com.example.todoapp.domain.model.User;
import com.example.todoapp.domain.port.in.UserUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.ArgumentMatchers.anyString;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserUseCase userUseCase;

    @Test
    void register_validBody_returns201WithToken() throws Exception {
        when(userUseCase.register("alice", "secret")).thenReturn(new User(UUID.randomUUID(), "alice", "hash"));
        when(userUseCase.login("alice", "secret")).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "alice", "password": "secret"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void register_blankUsername_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "", "password": "secret"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("username: " + RegisterRequest.BLANK_MESSAGE));
    }

    @Test
    void register_blankPassword_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "alice", "password": ""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("password: " + RegisterRequest.BLANK_MESSAGE));
    }

    @Test
    void register_takenUsername_returns409() throws Exception {
        when(userUseCase.register(anyString(), anyString())).thenThrow(new UsernameAlreadyTakenException());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "bob", "password": "anything"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value(UsernameAlreadyTakenException.MESSAGE));
    }

    @Test
    void login_validCredentials_returns200WithToken() throws Exception {
        when(userUseCase.login("carol", "pass456")).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "carol", "password": "pass456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_wrongPassword_returns401() throws Exception {
        when(userUseCase.login(anyString(), anyString())).thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "dave", "password": "wrong"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value(InvalidCredentialsException.MESSAGE));
    }
}
