package com.bsse1401_bsse1429.TimeWise.service;

import com.bsse1401_bsse1429.TimeWise.model.Task;
import com.bsse1401_bsse1429.TimeWise.repository.TaskRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private JWTService jwtService;

    // Create a new task
    public Task createTask(Task task, String userName) {

        task.setTaskOwner(userName);

        if (task.getTaskParticipants() == null) {
            task.setTaskParticipants(new ArrayList<>());
        }
        if (!task.getTaskParticipants().contains(userName)) {
            task.getTaskParticipants().add(userName);
        }

        if (task.getTaskCreationDate() == null) {
            task.setTaskCreationDate(new Date());
        }

        if (task.getTaskDeadline() == null) {
            // Convert to LocalDateTime, add 1 day, and convert back to Date
            LocalDateTime creationDate = task.getTaskCreationDate()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            LocalDateTime deadline = creationDate.plusDays(1); // Add 1 day
            task.setTaskDeadline(Date.from(deadline.atZone(ZoneId.systemDefault()).toInstant()));
        }

        if (task.getTaskCategory() == null || task.getTaskCategory().isEmpty()) {
            task.setTaskCategory("General");
        }
        if (task.getTaskDescription() == null || task.getTaskDescription().isEmpty()) {
            task.setTaskDescription("");
        }
        if (task.getTaskPriority() == null || task.getTaskPriority().isEmpty()) {
            task.setTaskPriority("Medium");
        }
        if (task.getTaskVisibilityStatus() == null || task.getTaskVisibilityStatus().isEmpty()) {
            task.setTaskVisibilityStatus("Private");
        }
        if (task.getTaskComments() == null) {
            task.setTaskComments(new ArrayList<>());
        }
        if (task.getTaskNotes() == null) {
            task.setTaskNotes(new TreeMap<>());
        }
        if (task.getTaskCurrentProgress() == null) {
            task.setTaskCurrentProgress(0);
        }
        if (task.getTaskModificationHistory() == null) {
            task.setTaskModificationHistory(new ArrayList<>());
        }

        return taskRepository.save(task);
    }

    // Add a comment to a task
    public Task addTaskComment(ObjectId taskId, String userName, String commentText) {
        Task task = getTaskById(taskId);
        if(task.getTaskParticipants().contains(userName)) {
            task.addTaskComment(userName, commentText);
            return taskRepository.save(task);
        }
        else{
            return null;
        }
    }
    // Add a Note to a task
    public Task addTaskNote(ObjectId taskId, String userName, String noteText) {
        Task task = getTaskById(taskId);
        if(task.getTaskParticipants().contains(userName)) {
            task.addTaskNote(userName, noteText);
            return taskRepository.save(task);
        }
        else{
            return null;
        }
    }
    // Modify a task attribute
    public Task modifyTaskAttribute(ObjectId taskId, String fieldName, String updatedBy, Object newValue) {
        Task task = getTaskById(taskId);
        if(fieldName.equals("taskCurrentProgress") && task.getTaskParticipants().contains(updatedBy)){
            task.updateTaskProgress(updatedBy,newValue);
            return taskRepository.save(task);
        }
        else if(!fieldName.equals("taskCurrentProgress") && task.getTaskOwner().equals(updatedBy)){
            task.modifyTaskAttribute(fieldName,updatedBy, newValue);
            return taskRepository.save(task);
        }
        else{
            return null;
        }

    }

    // Delete a task by ID
    public void deleteTask(ObjectId taskId, String userName) {
        Task task = getTaskById(taskId);
        if (task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with ID: " + taskId);
        } else if (!task.getTaskOwner().equals(userName)) {
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

    // Get all tasks of a User
    public List<Task> getAllTasksOfAnUser(String userName) {
        List<Task> tasks = taskRepository.findByTaskParticipantsContains(userName);

        tasks.sort((t1, t2) -> {
            double rank1 = calculateTaskRank(t1);
            double rank2 = calculateTaskRank(t2);
            return Double.compare(rank2, rank1); // Descending order (higher rank first)
        });

        return tasks;
    }

    private double calculateTaskRank(Task task) {
        // Priority contributes 40%
        double priorityWeight = 0.4;
        int priorityValue = getPriorityValue(task.getTaskPriority());

        // Deadline contributes 40%
        double deadlineWeight = 0.4;
        long currentTime = System.currentTimeMillis();
        long timeToDeadline = task.getTaskDeadline().getTime() - currentTime;
        double deadlineScore = timeToDeadline > 0 ? 1.0 / timeToDeadline : 0;

        // Progress contributes 20% - Invert progress so lower progress has higher priority
        double progressWeight = 0.2;
        double progressScore = 1.0 - (task.getTaskCurrentProgress() / 100.0); // Lower progress means higher score

        // Calculate total rank
        return (priorityValue * priorityWeight) +
                (deadlineScore * deadlineWeight) +
                (progressScore * progressWeight);
    }

    public List<Task> getAllTasksOfAnUserSortedByPriority(String userName) {
        List<Task> tasks = taskRepository.findByTaskParticipantsContains(userName);
        tasks.sort((t1, t2) -> {
            // Assuming priority is represented as "High", "Normal", "Low"
            int p1 = getPriorityValue(t1.getTaskPriority());
            int p2 = getPriorityValue(t2.getTaskPriority());
            return Integer.compare(p2, p1); // Descending order (High > Normal > Low)
        });
        return tasks;
    }

    private int getPriorityValue(String priority) {
        return switch (priority.toLowerCase()) {
            case "high" -> 3;
            case "medium" -> 2;
            case "low" -> 1;
            default -> 0; // For invalid or undefined priority
        };
    }

    public List<Task> getAllTasksOfAnUserSortedByDeadline(String userName) {
        List<Task> tasks = taskRepository.findByTaskParticipantsContains(userName);
        tasks.sort(Comparator.comparing(Task::getTaskDeadline)); // Ascending order (earliest deadline first)
        return tasks;
    }

    public List<Task> getAllTasksOfAnUserSortedByProgress(String userName) {
        List<Task> tasks = taskRepository.findByTaskParticipantsContains(userName);
        tasks.sort(Comparator.comparingInt(Task::getTaskCurrentProgress)); // Ascending order (lowest progress first)
        return tasks;
    }
}
