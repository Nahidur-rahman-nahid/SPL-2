package com.bsse1401_bsse1429.TimeWise.controller;

import com.bsse1401_bsse1429.TimeWise.model.Task;
import com.bsse1401_bsse1429.TimeWise.service.JWTService;
import com.bsse1401_bsse1429.TimeWise.service.TaskService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private JWTService jwtService;

    // Helper method to get the userId from the token directly (no HttpServletRequest)
    private ObjectId getUserIdFromToken(String token) {
        if (jwtService.verifyToken(token)) {
            return jwtService.extractUserId(token);
        }
        throw new RuntimeException("Invalid or missing token.");
    }

    // Create a new task
    @PostMapping("/createTask")
    public ResponseEntity<Task> createTask(@RequestBody Task task, @RequestParam String token) {
        ObjectId userId = getUserIdFromToken(token); // Verify and extract userId from token
        task.setTaskOwner(userId.toHexString()); // Assign task to the authenticated user
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.ok(createdTask);
    }

    // Get all tasks
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(@RequestParam String token) {
        getUserIdFromToken(token); // Verify the token and user
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    // Get a task by ID
    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable ObjectId taskId, @RequestParam String token) {
        getUserIdFromToken(token); // Verify the token and user
        Task task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }

    // Update a task's progress
    @PutMapping("/{taskId}/progress")
    public ResponseEntity<Task> updateTaskProgress(@PathVariable ObjectId taskId,
                                                   @RequestParam String updatedBy,
                                                   @RequestParam int newProgress,
                                                   @RequestParam String token) {
        ObjectId userId = getUserIdFromToken(token); // Verify and extract userId from token
        // Optionally, check if the user has permission to update this task (e.g., taskOwner check)
        Task updatedTask = taskService.updateTaskProgress(taskId, updatedBy, newProgress);
        return ResponseEntity.ok(updatedTask);
    }

    // Add a comment to a task
    @PostMapping("/{taskId}/comments")
    public ResponseEntity<Task> addTaskComment(@PathVariable ObjectId taskId,
                                               @RequestParam String username,
                                               @RequestParam String commentText,
                                               @RequestParam String token) {
        ObjectId userId = getUserIdFromToken(token); // Verify and extract userId
        // Optionally, check if the user has permission to comment on this task
        Task updatedTask = taskService.addTaskComment(taskId, username, commentText);
        return ResponseEntity.ok(updatedTask);
    }

    // Modify a task attribute
    @PutMapping("/{taskId}/modify")
    public ResponseEntity<Task> modifyTaskAttribute(@PathVariable ObjectId taskId,
                                                    @RequestParam String fieldName,
                                                    @RequestParam String updatedBy,
                                                    @RequestParam String newValue,
                                                    @RequestParam String token) {
        getUserIdFromToken(token); // Verify and extract userId
        Task updatedTask = taskService.modifyTaskAttribute(taskId, fieldName, updatedBy, newValue);
        return ResponseEntity.ok(updatedTask);
    }

    // Delete a task by ID
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable ObjectId taskId, @RequestParam String token) {
        getUserIdFromToken(token); // Verify and extract userId
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
