package com.bsse1401_bsse1429.TimeWise.service;

import com.bsse1401_bsse1429.TimeWise.model.Task;
import com.bsse1401_bsse1429.TimeWise.repository.TaskRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private JWTService jwtService;

    // Helper method to get the userId from the token directly (no HttpServletRequest)
//    private ObjectId getUserIdFromToken(String token) {
//        if (jwtService.verifyToken(token)) {
//            return jwtService.extractUserId(token);
//        }
//        throw new RuntimeException("Invalid or missing token.");
//    }

    // Create a new task
    public Task createTask(Task task,String userName) {
        task.setTaskOwner(userName);
        if (task.getTaskParticipants() == null) {
            task.setTaskParticipants(new ArrayList<>());
        }
        task.getTaskParticipants().add(userName);
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
    public void deleteTask(ObjectId taskId,String userName) {
        Task task = getTaskById(taskId);
        if (task==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with ID: " + taskId);
        }
        else if(!task.getTaskOwner().equals(userName)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not the owner of the owner of this task");
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
    public List<Task> getAllTasksOfAnUser(String userName) {
        return taskRepository.findByTaskParticipantsContains(userName);
    }
}
