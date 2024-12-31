package com.bsse1401_bsse1429.TimeWise.controller;

import com.bsse1401_bsse1429.TimeWise.model.Task;
import com.bsse1401_bsse1429.TimeWise.utils.AddTaskCommentRequestBody;
import com.bsse1401_bsse1429.TimeWise.utils.AddTaskNoteRequestBody;
import com.bsse1401_bsse1429.TimeWise.utils.TaskModificationRequestBody;
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

    // Get all tasks of a User
    @GetMapping("/allTasks")
    public ResponseEntity<List<Task>> allTasks(@RequestParam String userName) {
        List<Task> tasks = taskService.getAllTasksOfAnUser(userName);
        return ResponseEntity.ok(tasks);
    }

    // Get all tasks sorted on priority (high, normal, low)
    @GetMapping("/allTasks/sortedbypriority")
    public ResponseEntity<List<Task>> allTasksSortedByPriority(@RequestParam String userName) {
        List<Task> tasks = taskService.getAllTasksOfAnUserSortedByPriority(userName);
        return ResponseEntity.ok(tasks);
    }

    // Get all tasks sorted on deadline
    @GetMapping("/allTasks/sortedbydeadline")
    public ResponseEntity<List<Task>> allTasksSortedByDeadline(@RequestParam String userName) {
        List<Task> tasks = taskService.getAllTasksOfAnUserSortedByDeadline(userName);
        return ResponseEntity.ok(tasks);
    }

    // Get all tasks sorted on progress
    @GetMapping("/allTasks/sortedbyprogress")
    public ResponseEntity<List<Task>> allTasksSortedByProgress(@RequestParam String userName) {
        List<Task> tasks = taskService.getAllTasksOfAnUserSortedByProgress(userName);
        return ResponseEntity.ok(tasks);
    }

    // Delete Task
    @PostMapping("/deletetask")
    public ResponseEntity<Void> deleteTask(@RequestBody ObjectId taskId, @RequestParam String userName) {
        taskService.deleteTask(taskId, userName);
        return ResponseEntity.noContent().build();
    }

    // Add Task Comment
    @PostMapping("/addtaskcomment")
    public ResponseEntity<Task> addTaskComment(@RequestBody AddTaskCommentRequestBody addTaskCommentRequestBody) {
        Task updatedTask = taskService.addTaskComment(addTaskCommentRequestBody.getTaskId(), addTaskCommentRequestBody.getUserName(), addTaskCommentRequestBody.getTaskComment());
        if (updatedTask != null) {
            return ResponseEntity.ok(updatedTask);
        }
        return ResponseEntity.badRequest().body(null);
    }

    // Add Task Note
    @PostMapping("/addtasknote")
    public ResponseEntity<Task> addTaskNote(@RequestBody AddTaskNoteRequestBody addTaskNoteRequestBody) {
        Task updatedTask = taskService.addTaskNote(addTaskNoteRequestBody.getTaskId(), addTaskNoteRequestBody.getUserName(), addTaskNoteRequestBody.getTaskNote());
        if (updatedTask != null) {
            return ResponseEntity.ok(updatedTask);
        }
        return ResponseEntity.badRequest().body(null);
    }

    // Modify a Task
    @PostMapping("/modifytask")
    public ResponseEntity<Task> modifyTaskAttribute(@RequestBody TaskModificationRequestBody taskModificationRequestBody) {
        Task updatedTask = taskService.modifyTaskAttribute(taskModificationRequestBody.getTaskId(), taskModificationRequestBody.getFieldName(), taskModificationRequestBody.getUpdatedBy(),taskModificationRequestBody.getNewValue());
        if (updatedTask != null) {
            return ResponseEntity.ok(updatedTask);
        }
        return ResponseEntity.badRequest().body(null);
    }
}
