package com.martin.todo_list.services;

import com.martin.todo_list.dto.TaskDTO;
import com.martin.todo_list.entities.Task;
import com.martin.todo_list.entities.TaskStatus;
import com.martin.todo_list.entities.User;
import com.martin.todo_list.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskDTO> getAllTasks(User user) {
        List<Task> tasks = taskRepository.findAllByUser(user);

        return tasks.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksByStatus(User user, TaskStatus status) {
        List<Task> tasks = taskRepository.findAllByUserAndStatus(user, status);
        return tasks.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public TaskDTO saveTask(TaskDTO taskDTO, User user) {
        Task task = mapToEntity(taskDTO);
        task.setUser(user);
        Task savedTask = taskRepository.save(task);

        return mapToDTO(savedTask);
    }

    public TaskDTO updateTask(Integer id, TaskDTO newTask, User user){
        Task oldTask = taskRepository.findById(id).orElse(null);
        if (oldTask != null && oldTask.getUser().getId().equals(user.getId())){
            oldTask.setTitle(newTask.getTitle());
            oldTask.setDescription(newTask.getDescription());
            oldTask.setDueDate(newTask.getDueDate());
            oldTask.setStatus(newTask.getStatus());

            Task savedTask = taskRepository.save(oldTask);
            return mapToDTO(savedTask);
        }
        return null;
    }

    public TaskDTO updateTaskStatus(Integer id, TaskStatus newStatus, User user){
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null && task.getUser().getId().equals(user.getId())) {
            task.setStatus(newStatus);
            Task savedTask = taskRepository.save(task);
            return mapToDTO(savedTask);
        }
        return null;
    }

    public boolean deleteTask(Integer id, User user) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null && task.getUser().getId().equals(user.getId())) {
            taskRepository.delete(task);
            return true;
        }
        return false;
    }

    public TaskDTO getTaskById(Integer id, User user) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null && task.getUser().getId().equals(user.getId())) {
            return mapToDTO(task);
        }
        return null;
    }

    private Task mapToEntity(TaskDTO taskDTO) {
        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());
        return task;
    }

    private TaskDTO mapToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setDueDate(task.getDueDate());
        dto.setStatus(task.getStatus());
        dto.setCreatedAt(task.getCreatedAt());
        return dto;
    }
}
