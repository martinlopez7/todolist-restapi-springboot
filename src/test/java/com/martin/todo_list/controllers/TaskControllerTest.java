package com.martin.todo_list.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martin.todo_list.dto.TaskDTO;
import com.martin.todo_list.entities.TaskStatus;
import com.martin.todo_list.entities.User;
import com.martin.todo_list.repositories.UserRepository;
import com.martin.todo_list.services.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Optional;

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

    @MockitoBean
    private UserRepository userRepository;

    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    @WithMockUser(username = "usuarioTest") // 1. Simulamos que "usuarioTest" está logueado
    void shouldCreateTaskAndReturn201() throws Exception {
        // Datos de entrada
        TaskDTO inputDTO = new TaskDTO();
        inputDTO.setTitle("Nueva Tarea");
        inputDTO.setDescription("Test Controller");

        // Datos de salida
        TaskDTO outputDTO = new TaskDTO();
        outputDTO.setId(1);
        outputDTO.setTitle("Nueva Tarea");
        outputDTO.setStatus(TaskStatus.PENDING);

        // Usuario Mock
        User mockUser = new User();
        mockUser.setId(99);
        mockUser.setUsername("usuarioTest");

        // 2. Comportamiento del UserRepository
        // Cuando el controller busque "usuarioTest", le damos nuestro mock
        when(userRepository.findByUsername("usuarioTest")).thenReturn(Optional.of(mockUser));

        // 3. Comportamiento del Service
        // Ahora el servicio espera recibir un User, usamos any(User.class)
        when(taskService.saveTask(any(TaskDTO.class), any(User.class))).thenReturn(outputDTO);

        // 4. Ejecución
        mockMvc.perform(post("/tasks")
                        .with(csrf()) // IMPORTANTE: En tests WebMvc con seguridad, hay que añadir el token CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Nueva Tarea"))
                .andExpect(jsonPath("$.id").value(1));
    }
}
