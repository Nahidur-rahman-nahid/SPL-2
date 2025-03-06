package com.TimeWise.controller;

import com.TimeWise.model.Task;
import com.TimeWise.utils.AddTaskCommentRequestBody;
import com.TimeWise.utils.AddTaskNoteRequestBody;
import com.TimeWise.utils.AddTaskTodoRequestBody;
import com.TimeWise.utils.TaskModificationRequestBody;
import com.TimeWise.service.TaskService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        List<Task> createdTasks = taskService.createTask(tasks);
        return ResponseEntity.ok(createdTasks);
    }

    // Get all tasks of a User
    @GetMapping("/all")
    public ResponseEntity<?> allTasks() {

        List<Task> tasks = taskService.getAllTasksOfAnUser();
        if(tasks==null || tasks.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No tasks found");

        }
        return ResponseEntity.ok(tasks);
    }

    // Get all tasks sorted on priority (high, normal, low)
    @GetMapping("/all/sort/priority")
    public ResponseEntity<?> allTasksSortedByPriority() {

        List<Task> tasks = taskService.getAllTasksOfAnUserSortedByPriority();
        if(tasks==null || tasks.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No tasks found");

        }
        return ResponseEntity.ok(tasks);
    }

    // Get all tasks sorted on deadline
    @GetMapping("/all/sort/deadline")
    public ResponseEntity<?> allTasksSortedByDeadline() {

        List<Task> tasks = taskService.getAllTasksOfAnUserSortedByDeadline();
        if(tasks==null || tasks.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No tasks found");

        }
        return ResponseEntity.ok(tasks);
    }

    // Get all tasks sorted on progress
    @GetMapping("/all/sort/progress")
    public ResponseEntity<?> allTasksSortedByProgress() {

        List<Task> tasks = taskService.getAllTasksOfAnUserSortedByProgress();
        if(tasks==null || tasks.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No tasks found");

        }
        return ResponseEntity.ok(tasks);
    }

    // Get all tasks sorted on progress
    @GetMapping("/all/sort/goal")
    public ResponseEntity<?> allTasksSortedByGoal() {

        List<Task> tasks = taskService.getAllTasksOfAnUserSortedByGoal();
        if(tasks==null || tasks.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No tasks found");

        }
        return ResponseEntity.ok(tasks);
    }

    // Delete Task
    @PostMapping("/delete")
    public ResponseEntity<?> deleteTask(@RequestParam String taskName) {

        return ResponseEntity.ok(taskService.deleteTask(taskName));
    }

    // Add Task Comment
    @PostMapping("/add/comment")
    public ResponseEntity<Task> addTaskComment(@RequestBody AddTaskCommentRequestBody addTaskCommentRequestBody) {
        Task updatedTask = taskService.addTaskComment(addTaskCommentRequestBody.getTaskId(), addTaskCommentRequestBody.getTaskComment());
        if (updatedTask != null) {
            return ResponseEntity.ok(updatedTask);
        }
        return ResponseEntity.badRequest().body(null);
    }

    // Add Task Note
    @PostMapping("/add/note")
    public ResponseEntity<Task> addTaskNote(@RequestBody AddTaskNoteRequestBody addTaskNoteRequestBody) {
        Task updatedTask = taskService.addTaskNote(addTaskNoteRequestBody.getTaskId(), addTaskNoteRequestBody.getTaskNote());
        if (updatedTask != null) {
            return ResponseEntity.ok(updatedTask);
        }
        return ResponseEntity.badRequest().body(null);
    }

    // Add Task To Do
    @PostMapping("/add/todo")
    public ResponseEntity<Task> addTaskTodo(@RequestBody AddTaskTodoRequestBody addTaskTodoRequestBody) {
        Task updatedTask = taskService.addTaskTodo(addTaskTodoRequestBody.getTaskId(), addTaskTodoRequestBody.getTaskTodo());
        if (updatedTask != null) {
            return ResponseEntity.ok(updatedTask);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/modify")
    public ResponseEntity<Task> modifyTaskAttribute(@RequestBody TaskModificationRequestBody taskModificationRequestBody) {
        Task updatedTask = taskService.modifyTaskAttribute(taskModificationRequestBody.getTaskId(), taskModificationRequestBody.getFieldName(), taskModificationRequestBody.getNewValue());
        if (updatedTask != null) {
            return ResponseEntity.ok(updatedTask);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTaskDetails(@PathVariable String taskName,@PathVariable String taskOwner) {
        return taskService.getTaskDetails(taskName,taskOwner);
    }


}
