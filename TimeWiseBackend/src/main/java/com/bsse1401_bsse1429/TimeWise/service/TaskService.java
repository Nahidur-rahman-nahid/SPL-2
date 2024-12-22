package com.bsse1401_bsse1429.TimeWise.service;

import com.bsse1401_bsse1429.TimeWise.model.Task;
import com.bsse1401_bsse1429.TimeWise.repository.TaskRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    // Create a new task
    public Task createTask(Task task) {
        task.setTaskCreationDate(new Date());
        return taskRepository.save(task);
    }

    // Add a comment to a task
    public Task addTaskComment(ObjectId taskId, String username, String commentText) {
        Task task = getTaskById(taskId);
        task.addTaskComment(username, commentText); // Assuming Task model has this method
        return taskRepository.save(task);
    }

    // Modify a task attribute
    public Task modifyTaskAttribute(ObjectId taskId, String fieldName, String updatedBy, String newValue) {
        Task task = getTaskById(taskId);
        task.modifyTaskAttribute(fieldName, updatedBy, newValue); // Assuming Task model has this method
        return taskRepository.save(task);
    }

    // Update task progress
    public Task updateTaskProgress(ObjectId taskId, String updatedBy, int newProgress) {
        Task task = getTaskById(taskId);
        task.updateTaskProgress(updatedBy, newProgress); // Assuming Task model has this method
        return taskRepository.save(task);
    }

    // Delete a task by ID
    public void deleteTask(ObjectId taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with ID: " + taskId);
        }
        taskRepository.deleteById(taskId);
    }

    // Get a task by ID
    public Task getTaskById(ObjectId taskId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with ID: " + taskId);
        }
        return taskOptional.get();
    }

    // Get all tasks
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}
