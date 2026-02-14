package com.martin.todo_list.repositories;

import com.martin.todo_list.entities.Task;
import com.martin.todo_list.entities.TaskStatus;
import com.martin.todo_list.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Integer> {

    List<Task> findAllByUser(User user);
    List<Task> findAllByUserAndStatus(User user, TaskStatus status);
}
