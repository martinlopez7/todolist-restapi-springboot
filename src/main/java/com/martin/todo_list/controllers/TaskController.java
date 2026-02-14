package com.martin.todo_list.controllers;

import com.martin.todo_list.dto.TaskDTO;
import com.martin.todo_list.entities.Task;
import com.martin.todo_list.entities.TaskStatus;
import com.martin.todo_list.entities.User;
import com.martin.todo_list.repositories.UserRepository;
import com.martin.todo_list.services.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getTasks(@RequestParam(required = false) TaskStatus status){
        User user = getAuthenticatedUser();
        List<TaskDTO> tasksDTO;
        if (status != null) {
            tasksDTO = taskService.getTasksByStatus(user, status);
        } else {
            tasksDTO = taskService.getAllTasks(user);
        }
        return ResponseEntity.ok(tasksDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Integer id) {
        User user = getAuthenticatedUser();
        TaskDTO taskDTO = taskService.getTaskById(id, user);
        if(taskDTO != null) {
            return ResponseEntity.ok().body(taskDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<TaskDTO> postTask(@Valid @RequestBody TaskDTO taskDTO){
        User user = getAuthenticatedUser();
        TaskDTO newTask = taskService.saveTask(taskDTO, user);
        return new ResponseEntity<>(newTask, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> putTask(@PathVariable Integer id, @Valid @RequestBody TaskDTO newTask){
        User user = getAuthenticatedUser();
        TaskDTO updatedTask = taskService.updateTask(id, newTask, user);
        if (updatedTask != null) {
            return ResponseEntity.ok().body(updatedTask);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskDTO> patchTask(@PathVariable Integer id, @Valid @RequestParam TaskStatus status){
        User user = getAuthenticatedUser();
        TaskDTO updatedTask = taskService.updateTaskStatus(id, status, user);

        if (updatedTask != null) {
            return ResponseEntity.ok(updatedTask);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Task> deleteTask(@PathVariable Integer id){
        User user = getAuthenticatedUser();
        boolean isDeleted = taskService.deleteTask(id, user);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
