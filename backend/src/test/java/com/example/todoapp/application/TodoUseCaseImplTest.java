package com.example.todoapp.application;

import com.example.todoapp.domain.model.Todo;
import com.example.todoapp.domain.port.out.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoUseCaseImplTest {

    @Mock
    TodoRepository repository;

    @InjectMocks
    TodoUseCaseImpl todoUseCase;

    @Test
    void getAll_returnsAllTodos() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        when(repository.findAll()).thenReturn(List.of(
                new Todo(id1, "Buy milk", false),
                new Todo(id2, "Walk the dog", true)
        ));

        List<Todo> result = todoUseCase.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(id1, result.get(0).getId());
        assertEquals("Buy milk", result.get(0).getTitle());
        assertFalse(result.get(0).isCompleted());
        assertEquals(id2, result.get(1).getId());
        assertEquals("Walk the dog", result.get(1).getTitle());
        assertTrue(result.get(1).isCompleted());
    }

    @Test
    void create_savesAndReturnsTodo() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Todo result = todoUseCase.create("Buy milk");

        ArgumentCaptor<Todo> captor = ArgumentCaptor.forClass(Todo.class);
        verify(repository).save(captor.capture());

        assertNotNull(captor.getValue());
        assertEquals("Buy milk", captor.getValue().getTitle());
        assertFalse(captor.getValue().isCompleted());
        assertNotNull(captor.getValue().getId());

        assertNotNull(result);
        assertEquals("Buy milk", result.getTitle());
        assertFalse(result.isCompleted());
        assertNotNull(result.getId());
    }

    @Test
    void toggle_whenTodoIsNotCompleted_returnsCompletedTodo() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(new Todo(id, "Buy milk", false)));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Todo result = todoUseCase.toggle(id);

        assertNotNull(result);
        assertTrue(result.isCompleted());
        assertEquals(id, result.getId());
        assertEquals("Buy milk", result.getTitle());
    }

    @Test
    void toggle_whenTodoIsCompleted_returnsNotCompletedTodo() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(new Todo(id, "Buy milk", true)));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Todo result = todoUseCase.toggle(id);

        assertNotNull(result);
        assertFalse(result.isCompleted());
        assertEquals(id, result.getId());
        assertEquals("Buy milk", result.getTitle());
    }

    @Test
    void toggle_whenTodoDoesNotExist_throwsNoSuchElementException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> todoUseCase.toggle(id));
    }
}
