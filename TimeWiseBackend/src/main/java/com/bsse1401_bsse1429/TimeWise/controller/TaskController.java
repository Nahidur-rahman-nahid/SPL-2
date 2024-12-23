package com.bsse1401_bsse1429.TimeWise.controller;

import com.bsse1401_bsse1429.TimeWise.model.Task;
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

    // Create Task
    @PostMapping("/createTask")
    public ResponseEntity<Task> createTask(@RequestBody Task task, @RequestParam String userName) {
        Task createdTask = taskService.createTask(task, userName);
        return ResponseEntity.ok(createdTask);
    }

    // Get all tasks (use query parameter for GET)
    @GetMapping("/allTasks")
    public ResponseEntity<List<Task>> allTasks(@RequestParam String userName) {
        List<Task> tasks = taskService.getAllTasksOfAnUser(userName);
        return ResponseEntity.ok(tasks);
    }
//
//    // Get Task by ID
//    @GetMapping("/{taskId}")
//    public ResponseEntity<Task> getTaskById(@PathVariable ObjectId taskId, @RequestParam String username) {
//        Task task = taskService.getTaskById(taskId, username);
//        return ResponseEntity.ok(task);
//    }

//    // Update Task Progress
//    @PostMapping("/{taskId}/progress")
//    public ResponseEntity<Task> updateTaskProgress(@PathVariable ObjectId taskId, @RequestBody UpdateTaskProgressRequest updateRequest) {
//        Task updatedTask = taskService.updateTaskProgress(
//                taskId,
//                updateRequest.getUpdatedBy(),
//                updateRequest.getNewProgress(),
//                updateRequest.getUsername()
//        );
//        return ResponseEntity.ok(updatedTask);
//    }
//
//    // Add Task Comment
//    @PostMapping("/{taskId}/comments")
//    public ResponseEntity<Task> addTaskComment(@PathVariable ObjectId taskId, @RequestBody AddCommentRequest commentRequest) {
//        Task updatedTask = taskService.addTaskComment(taskId, commentRequest.getUsername(), commentRequest.getCommentText());
//        return ResponseEntity.ok(updatedTask);
//    }
//
//    // Modify Task Attribute
//    @PostMapping("/{taskId}/modify")
//    public ResponseEntity<Task> modifyTaskAttribute(@PathVariable ObjectId taskId, @RequestBody ModifyTaskRequest modifyRequest) {
//        Task updatedTask = taskService.modifyTaskAttribute(
//                taskId,
//                modifyRequest.getFieldName(),
//                modifyRequest.getUpdatedBy(),
//                modifyRequest.getNewValue(),
//                modifyRequest.getUsername()
//        );
//        return ResponseEntity.ok(updatedTask);
//    }

    // Delete Task
    @PostMapping("/{taskId}/delete")
    public ResponseEntity<Void> deleteTask(@PathVariable ObjectId taskId, @RequestParam String userName) {
        taskService.deleteTask(taskId, userName);
        return ResponseEntity.noContent().build();
    }
}
