package com.example.todoapp.adapter.in.http;

import com.example.todoapp.domain.model.Todo;
import com.example.todoapp.domain.port.in.TodoUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TodoUseCase todoUseCase;

    @Test
    void getAllTodos_returns200WithTodos() throws Exception {
        when(todoUseCase.getAll()).thenReturn(List.of(
                new Todo(null, "Buy milk", false),
                new Todo(null, "Walk the dog", false)
        ));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Buy milk"))
                .andExpect(jsonPath("$[1].title").value("Walk the dog"));
    }

    @Test
    void postTodo_returns201WithTitleAndCompleted() throws Exception {
        when(todoUseCase.create("Buy milk", null))
                .thenReturn(new Todo(null, "Buy milk", false, null));

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Buy milk"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Buy milk"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.dueDate").isEmpty());
    }

    @Test
    void patchToggle_returns200WithUpdatedTodo() throws Exception {
        UUID id = UUID.randomUUID();
        boolean anyCompletedState = true;
        when(todoUseCase.toggle(id)).thenReturn(new Todo(id, "Buy milk", anyCompletedState));

        mockMvc.perform(patch("/api/todos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("Buy milk"))
                .andExpect(jsonPath("$.completed").value(anyCompletedState))
                .andExpect(jsonPath("$.dueDate").isEmpty());
    }

    @Test
    void postTodo_withDueDate_returns201WithDueDate() throws Exception {
        when(todoUseCase.create("Submit report", "2026-05-10"))
                .thenReturn(new Todo(null, "Submit report", false, "2026-05-10"));

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Submit report", "dueDate": "2026-05-10"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Submit report"))
                .andExpect(jsonPath("$.dueDate").value("2026-05-10"));
    }

}
