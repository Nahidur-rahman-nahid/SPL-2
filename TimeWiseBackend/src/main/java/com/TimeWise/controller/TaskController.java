package com.TimeWise.controller;

import com.TimeWise.model.Task;
import com.TimeWise.utils.*;
import com.TimeWise.service.TaskService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // Create Task
    @PostMapping("/create")
    public ResponseEntity<?> createTask(@RequestBody List<Task> tasks) {
        return taskService.createTask(tasks);
    }
    // Invite Users to Task
    @PostMapping("/user/invite")
    public ResponseEntity<?> inviteMembers(
            @RequestParam String taskName,
            @RequestParam String recipient) {
        return taskService.inviteMembers(taskName,recipient);
    }

    @PutMapping("/user/invite/response")
    public ResponseEntity<?> handleInvitationResponse(
            @RequestParam String taskName,
            @RequestParam String taskOwner,
            @RequestParam String response) {
        return taskService.handleInvitationResponse(taskName,taskOwner,response);
    }

    @PostMapping("/todo/status")
    public ResponseEntity<?> updateTaskTodoStatus(@RequestBody TaskTodoStatusModificationRequestBody taskTodoStatusModificationRequestBody) {
        return taskService.updateTaskTodoStatus(taskTodoStatusModificationRequestBody);
    }

    // Get all tasks of a User
    @GetMapping("/all")
    public ResponseEntity<?> allTasks() {

        return taskService.getAllTasksSummaryOfAnUser();

    }

    // Get a tasks details
    @GetMapping("/details")
    public ResponseEntity<?> getTaskDetails(@RequestParam String taskName,@RequestParam String taskOwner) {

        return taskService.getTaskDetails(taskName,taskOwner);

    }

    // Delete Task
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteTask(@RequestParam String taskName) {

        return taskService.deleteTask(taskName);
    }

    // Add Task Comment
    @PostMapping("/comment/add")
    public ResponseEntity<?> addTaskComment(@RequestBody AddTaskCommentRequestBody requestBody) {
        return taskService.addTaskComment(requestBody.getTaskName(),requestBody.getTaskOwner(), requestBody.getTaskComment());

    }

    @PostMapping("/comment/delete")
    public ResponseEntity<?> deleteTaskComment(@RequestBody DeleteTaskCommentRequestBody requestBody) {
        return taskService.deleteTaskComment(requestBody.getTaskName(), requestBody.getTaskOwner(), requestBody.getTaskComment());
    }

    // Add Task Note
    @PostMapping("/note/add")
    public ResponseEntity<?> addTaskNote(@RequestBody AddTaskNoteRequestBody requestBody) {
        return taskService.addTaskNote(requestBody.getTaskName(), requestBody.getTaskOwner(),requestBody.getTaskNote());

    }
    @PostMapping("/note/delete")
    public ResponseEntity<?> deleteTaskNote(@RequestBody DeleteTaskNoteRequestBody requestBody) {
        return taskService.deleteTaskNote(requestBody.getTaskName(), requestBody.getTaskOwner(), requestBody.getTaskNote());
    }

    // Add Task To Do
    @PostMapping("/todo/add")
    public ResponseEntity<?> addTaskTodo(@RequestBody TaskTodoRequestBody requestBody) {
        return taskService.addTaskTodo(requestBody.getTaskName(), requestBody.getTaskOwner(),requestBody.getTaskTodo());

    }

    @PostMapping("/todo/delete")
    public ResponseEntity<?> deleteTaskTodo(@RequestBody TaskTodoStatusModificationRequestBody requestBody) {
        return taskService.deleteTaskTodo(requestBody.getTaskName(), requestBody.getTaskOwner(), requestBody.getUpdatedTaskTodoStatus());
    }

    @PostMapping("/modify")
    public ResponseEntity<?> modifyTaskAttribute(@RequestBody TaskDetailsModificationRequestBody requestBody) {

        return taskService.modifyTaskAttribute(requestBody);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getTaskProfile(@RequestParam String taskName,@RequestParam String taskOwner) {
        return taskService.getTaskProfile(taskName,taskOwner);
    }


}
