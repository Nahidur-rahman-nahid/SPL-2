package com.TimeWise.service;

import com.TimeWise.engine.CollaborationEngine;
import com.TimeWise.model.Task;
import com.TimeWise.repository.TaskRepository;
import com.TimeWise.utils.TaskDetailResponse;
import com.TimeWise.utils.UserCredentials;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public List<Task> createTask(List<Task> tasks) {
        String  userName=UserCredentials.getCurrentUsername();
        List<Task> userTasks=taskRepository.findByTaskOwner(userName);
//        List<Task> userTasks=taskRepository.findByTaskOwner(userName);
      List<String> userTasksNames=new ArrayList<>();
      for(Task task:userTasks){
          userTasksNames.add(task.getTaskName());
      }
//        for(Task task:tasks){
//            userTasksNames.add(task.getTaskName());
//        }
        if(tasks.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No tasks is passed");
        }
        List<Task> validTasks=new ArrayList<>();
        for(Task task:tasks) {

            if (task.getTaskName() == null || task.getTaskGoal() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task name and task goal can not be empty");
            }
            if(userTasksNames.contains(task.getTaskName())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You already have a task with that task name");
            }

            task.setTaskOwner(userName);

            task.setTaskParticipants(new HashSet<>());

            task.getTaskParticipants().add(userName);


                task.setInvitedMembers(new HashSet<>());



                task.setTaskCreationDate(new Date());


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
                task.setTaskDescription("No task description added");
            }
            if (task.getTaskPriority() == null || task.getTaskPriority().isEmpty()) {
                task.setTaskPriority("Medium");
            }
            if (task.getTaskVisibilityStatus() == null || task.getTaskVisibilityStatus().isEmpty()) {
                task.setTaskVisibilityStatus("Public");
            }
            if (task.getTaskGoal() == null || task.getTaskGoal().isEmpty()) {
                task.setTaskGoal("General");
            }

                task.setInvitedMembers(new HashSet<>());


                task.setTaskComments(new ArrayList<>());


                task.setTaskNotes(new TreeMap<>());


                task.setTaskTodos(new ArrayList<>());


                task.setTaskCurrentProgress(0);

                task.setTaskModificationHistory(new ArrayList<>());


            validTasks.add(task);
        }
        if(validTasks.size()==tasks.size()) {
            taskRepository.saveAll(tasks);
            return tasks;
        }
        return null;
    }

    // Generate a task using AI
//    public Task generateTask(GenerateTaskRequestBody task, String userName) {
//
//    }

    // Add a comment to a task
    public Task addTaskComment(ObjectId taskId, String commentText) {
        String  userName=UserCredentials.getCurrentUsername();
        Task task = taskRepository.findByTaskId(taskId);
        if(task==null){
            return null;
        }
        if(task.getTaskParticipants().contains(userName)) {
            task.addTaskComment(userName, commentText);
            return taskRepository.save(task);
        }
        else{
            return null;
        }
    }
    // Add a Note to a task
    public Task addTaskNote(ObjectId taskId, String noteText) {
        String  userName=UserCredentials.getCurrentUsername();
        Task task = taskRepository.findByTaskId(taskId);
        if(task==null){
            return null;
        }
        if(task.getTaskParticipants().contains(userName)) {
            task.addTaskNote(userName, noteText);
            return taskRepository.save(task);
        }
        else{
            return null;
        }
    }
    // Add a Note to a task
    public Task addTaskTodo(ObjectId taskId, String todoDescription) {
        String  userName=UserCredentials.getCurrentUsername();
        Task task = taskRepository.findByTaskId(taskId);
        if(task==null){
            return null;
        }
        if(task.getTaskOwner().equals(userName)) {
            task.addTaskTodo(userName, todoDescription);
            return taskRepository.save(task);
        }
        else{
            return null;
        }
    }
    // Modify a task attribute
    public Task modifyTaskAttribute(ObjectId taskId, String fieldName, Object newValue) {
        if(fieldName==null){
            return null;
        }
        String updatedBy =UserCredentials.getCurrentUsername();
        Task task = taskRepository.findByTaskId(taskId);
        if(task==null){
            return null;
        }
        if(fieldName.equals("taskCurrentProgress") && task.getTaskParticipants().contains(updatedBy)){
            task.updateTaskProgress(updatedBy,newValue);
            return taskRepository.save(task);
        }
        else if(task.getTaskOwner().equals(updatedBy)){
            task.modifyTaskAttribute(fieldName,updatedBy, newValue);
            return taskRepository.save(task);
        }
        else{
            return null;
        }

    }

    // Delete a task by task name
    public String deleteTask(String taskName) {
        String  userName=UserCredentials.getCurrentUsername();
        String response=CollaborationEngine.removeDeletedTaskFromTeams(taskName,userName);
        taskRepository.deleteByTaskOwnerAndTaskName(userName,taskName);
        return response;
    }


    // Get all tasks of a User
    public List<Task> getAllTasksOfAnUser() {
        String  userName = UserCredentials.getCurrentUsername();
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
        int priorityValue = switch (task.getTaskPriority().toLowerCase()) {
            case "high" -> 3;
            case "medium" -> 2;
            case "low" -> 1;
            default -> 0;
        };

        // Progress contributes 30%
        double progressWeight = 0.3;
        double progressValue = task.getTaskCurrentProgress() / 100.0; // Assuming progress is a percentage (0-100)

        // Deadline contributes 30%
        double deadlineWeight = 0.3;
        long currentTime = System.currentTimeMillis();
        long deadlineTime = task.getTaskDeadline().getTime();
        double deadlineValue = deadlineTime > currentTime ? 1.0 - (deadlineTime - currentTime) / (double) (deadlineTime - task.getTaskCreationDate().getTime()) : 0.0;

        // Calculate total rank
        return (priorityWeight * priorityValue) + (progressWeight * progressValue) + (deadlineWeight * deadlineValue);
    }


    public List<Task> getAllTasksOfAnUserSortedByPriority() {
        String  userName=UserCredentials.getCurrentUsername();
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

    public List<Task> getAllTasksOfAnUserSortedByDeadline() {
        String  userName=UserCredentials.getCurrentUsername();
        List<Task> tasks = taskRepository.findByTaskParticipantsContains(userName);
        tasks.sort(Comparator.comparing(Task::getTaskDeadline)); // Ascending order (earliest deadline first)
        return tasks;
    }

    public List<Task> getAllTasksOfAnUserSortedByProgress() {
        String  userName=UserCredentials.getCurrentUsername();
        List<Task> tasks = taskRepository.findByTaskParticipantsContains(userName);
        tasks.sort(Comparator.comparingInt(Task::getTaskCurrentProgress)); // Ascending order (lowest progress first)
        return tasks;
    }

    public ResponseEntity<?> getTaskDetails(String taskName,String taskOwner) {
        // Fetch the task from the database
        Task task = taskRepository.findByTaskNameAndTaskOwner(taskName,taskOwner);

        if(task==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }
        if ("private".equalsIgnoreCase(task.getTaskVisibilityStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task is private");
        }


            // Map only the required fields to a new DTO object
            TaskDetailResponse taskResponse = new TaskDetailResponse(
                    task.getTaskName(),
                    task.getTaskCategory(),
                    task.getTaskCreationDate(),
                    task.getTaskOwner(),
                    task.getTaskGoal(),
                    task.getTaskParticipants()
            );

            return ResponseEntity.ok(taskResponse);

    }


    public List<Task> getAllTasksOfAnUserSortedByGoal() {
        String userName = UserCredentials.getCurrentUsername(); // Get the current user's username
        List<Task> tasks = taskRepository.findByTaskParticipantsContains(userName); // Fetch tasks where the user is a participant
        tasks.sort(Comparator.comparing(Task::getTaskGoal, String.CASE_INSENSITIVE_ORDER)); // Sort alphabetically by task goal (case-insensitive)
        return tasks; // Return the sorted list
    }
}
