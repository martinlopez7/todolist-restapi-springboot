package com.martin.todo_list.controllers;

import com.martin.todo_list.dto.TaskDTO;
import com.martin.todo_list.entities.Task;
import com.martin.todo_list.entities.TaskStatus;
import com.martin.todo_list.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getTasks(@RequestParam(required = false) TaskStatus status){
        List<TaskDTO> tasksDTO;
        if (status != null) {
            tasksDTO = taskService.getTasksByStatus(status);
        } else {
            tasksDTO = taskService.getAllTasks();
        }
        return ResponseEntity.ok(tasksDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Integer id) {
        TaskDTO taskDTO = taskService.getTaskById(id);
        if(taskDTO != null) {
            return ResponseEntity.ok().body(taskDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<TaskDTO> postTask(@Valid @RequestBody TaskDTO taskDTO){
        TaskDTO newTask = taskService.saveTask(taskDTO);
        return new ResponseEntity<>(newTask, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> putTask(@PathVariable Integer id, @Valid @RequestBody TaskDTO newTask){
        TaskDTO updatedTask = taskService.updateTask(id, newTask);
        if (updatedTask != null) {
            return ResponseEntity.ok().body(updatedTask);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskDTO> patchTask(@PathVariable Integer id, @Valid @RequestParam TaskStatus status){
        TaskDTO updatedTask = taskService.updateTaskStatus(id, status);

        if (updatedTask != null) {
            return ResponseEntity.ok(updatedTask);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Task> deleteTask(@PathVariable Integer id){
        boolean isDeleted = taskService.deleteTask(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
