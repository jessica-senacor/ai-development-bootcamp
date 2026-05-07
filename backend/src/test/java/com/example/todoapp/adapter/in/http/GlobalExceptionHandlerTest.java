package com.example.todoapp.adapter.in.http;

import com.example.todoapp.domain.port.in.TodoUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TodoUseCase todoUseCase;

    @Test
    void noSuchElementException_returns404WithMessage() throws Exception {
        when(todoUseCase.getAll()).thenThrow(new NoSuchElementException("Todo not found"));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Todo not found"));
    }



    @Test
    void missingTitle_returns400WithFieldError() throws Exception {
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("title: " + CreateTodoRequest.TITLE_BLANK_MESSAGE));
    }

    @Test
    void unexpectedException_returns500WithoutInternalDetails() throws Exception {
        when(todoUseCase.getAll()).thenThrow(new RuntimeException("internal details must not leak"));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").value(GlobalExceptionHandler.UNEXPECTED_ERROR_MESSAGE));
    }

    @Test
    void malformedJson_returns400() throws Exception {
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not valid json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(GlobalExceptionHandler.MALFORMED_JSON_MESSAGE));
    }

    @Test
    void whitespaceOnlyTitle_returns400WithFieldError() throws Exception {
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "   "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("title: " + CreateTodoRequest.TITLE_BLANK_MESSAGE));
    }
    @Test
    void invalidDueDate_returns400() throws Exception {
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Buy milk", "dueDate": "not-a-date"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("dueDate: " + CreateTodoRequest.DUE_DATE_FORMAT_MESSAGE));
    }

    @Test
    void invalidUuidInPath_returns400() throws Exception {
        mockMvc.perform(patch("/api/todos/not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("id: invalid value 'not-a-uuid'"));
    }

    @Test
    void deleteTodo_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new NoSuchElementException("Todo not found")).when(todoUseCase).delete(id);

        mockMvc.perform(delete("/api/todos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Todo not found"));
    }

    @Test
    void deleteTodo_invalidUuid_returns400() throws Exception {
        mockMvc.perform(delete("/api/todos/not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("id: invalid value 'not-a-uuid'"));
    }

    @Test
    void emptyTitle_returns400WithExactDetail() throws Exception {
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": ""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("title: " + CreateTodoRequest.TITLE_BLANK_MESSAGE));
    }
}
