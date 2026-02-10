package com.martin.todo_list.services;

import com.martin.todo_list.dto.TaskDTO;
import com.martin.todo_list.entities.Task;
import com.martin.todo_list.entities.TaskStatus;
import com.martin.todo_list.repositories.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldCreateTaskSuccessfully() {
        TaskDTO inputDTO = new TaskDTO();
        inputDTO.setTitle("Test Tarea");
        inputDTO.setDescription("Probando con Mockito");
        inputDTO.setDueDate(LocalDate.now().plusDays(1));

        Task savedTask = new Task();
        savedTask.setId(1);
        savedTask.setTitle("Test Tarea");
        savedTask.setDescription("Probando con Mockito");
        savedTask.setDueDate(LocalDate.now().plusDays(1));
        savedTask.setCreatedAt(LocalDateTime.now());
        savedTask.setStatus(TaskStatus.PENDING);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskDTO result = taskService.saveTask(inputDTO);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Tarea", result.getTitle());
        assertEquals("Probando con Mockito", result.getDescription());
        assertEquals(LocalDate.now().plusDays(1), result.getDueDate());
        assertEquals(TaskStatus.PENDING, result.getStatus());
    }
}
