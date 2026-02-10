package com.martin.todo_list.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martin.todo_list.dto.TaskDTO;
import com.martin.todo_list.entities.TaskStatus;
import com.martin.todo_list.services.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void shouldCreateTaskAndReturn201() throws Exception {
        TaskDTO inputDTO = new TaskDTO();
        inputDTO.setTitle("Nueva Tarea");
        inputDTO.setDescription("Test Controller");

        TaskDTO outputDTO = new TaskDTO();
        outputDTO.setId(1);
        outputDTO.setTitle("Nueva Tarea");
        outputDTO.setStatus(TaskStatus.PENDING);

        when(taskService.saveTask(any(TaskDTO.class))).thenReturn(outputDTO);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Nueva Tarea"))
                .andExpect(jsonPath("$.id").value(1));
    }
}
