package com.TimeWise.service;

import com.TimeWise.engine.CollaborationEngine;
import com.TimeWise.model.Task;
import com.TimeWise.repository.TaskRepository;
import com.TimeWise.utils.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private JWTService jwtService;

    // Create a new task
    public ResponseEntity<?> createTask(List<Task> tasks) {
        String userName = UserCredentials.getCurrentUsername();

        if (tasks == null || tasks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No tasks provided.");
        }

        List<String> existingTaskNames = taskRepository.findByTaskOwner(userName)
                .stream()
                .map(Task::getTaskName)
                .toList();

        List<Task> validatedTasks = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        for (Task task : tasks) {
            if (task == null) {
                errorMessages.add("A task in the list is null.");
                continue; // Skip to the next task
            }

            if (task.getTaskName() == null || task.getTaskName().trim().isEmpty() || task.getTaskGoal() == null || task.getTaskGoal().trim().isEmpty()) {
                errorMessages.add("Task name and task goal cannot be empty for task: " + task.getTaskName());
                continue;
            }

            if (existingTaskNames.contains(task.getTaskName().trim())) {
                errorMessages.add("You already have a task with name: " + task.getTaskName().trim());
                continue;
            }

            if (task.getTaskTodos() == null || task.getTaskTodos().isEmpty()) {
                errorMessages.add("Task '" + task.getTaskName().trim() + "' should have at least one task todo.");
                continue;
            }

            Set<String> uniqueTodoDescriptions = new HashSet<>();
            for (Task.TaskTodo taskTodo : task.getTaskTodos()) {
                if (taskTodo == null || taskTodo.getDescription() == null || taskTodo.getDescription().trim().isEmpty()) {
                    errorMessages.add("A task todo description cannot be empty for task: " + task.getTaskName().trim());
                    continue;
                }
                if (uniqueTodoDescriptions.contains(taskTodo.getDescription().trim())) {
                    errorMessages.add("Task '" + task.getTaskName().trim() + "' should have unique task todo descriptions.");
                    continue;
                }
                taskTodo.setTimestamp(new Date());
                taskTodo.setStatus("Incomplete");
                uniqueTodoDescriptions.add(taskTodo.getDescription().trim());
            }

            task.setTaskName(task.getTaskName().trim());
            task.setTaskGoal(task.getTaskGoal().trim());
            task.setTaskOwner(userName);
            task.setTaskParticipants(new HashSet<>(Collections.singletonList(userName)));
            task.setInvitedMembers(new HashSet<>());
            task.setTaskCreationDate(new Date());

            if (task.getTaskDeadline() == null) {
                LocalDateTime deadline = task.getTaskCreationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusDays(1);
                task.setTaskDeadline(Date.from(deadline.atZone(ZoneId.systemDefault()).toInstant()));
            }

            task.setTaskCategory(Optional.ofNullable(task.getTaskCategory()).filter(s -> !s.trim().isEmpty()).orElse("General").trim());
            task.setTaskDescription(Optional.ofNullable(task.getTaskDescription()).filter(s -> !s.trim().isEmpty()).orElse("No task description added"));
            task.setTaskPriority(Optional.ofNullable(task.getTaskPriority()).filter(s -> !s.trim().isEmpty()).orElse("Medium"));
            task.setTaskVisibilityStatus(Optional.ofNullable(task.getTaskVisibilityStatus()).filter(s -> !s.trim().isEmpty()).orElse("Public"));

            task.setTaskComments(new ArrayList<>());
            task.setTaskNotes(new TreeMap<>());
            task.setTaskCurrentProgress(0);
            task.setTaskModificationHistory(new ArrayList<>());

            validatedTasks.add(task);
        }

        if (!errorMessages.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.join("\n", errorMessages));
        }

        taskRepository.saveAll(validatedTasks);
        return ResponseEntity.ok(validatedTasks);
    }
    // Generate a task using AI
//    public Task generateTask(GenerateTaskRequestBody task, String userName) {
//
//    }

    // Add a comment to a task
    public ResponseEntity<?> addTaskComment(String taskName,String taskOwner, String commentText) {
        String  userName= UserCredentials.getCurrentUsername();
        Task task = taskRepository.findByTaskNameAndTaskOwner(taskName,taskOwner);
        if(task==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }
        if(task.getTaskParticipants().contains(userName)) {
            task.addTaskComment(userName, commentText);
            taskRepository.save(task);
            return ResponseEntity.ok(task);
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not a participant of this task.");
        }
    }
    // Delete Task Comment
    public ResponseEntity<?> deleteTaskComment(String taskName, String taskOwner, Task.Comment taskCommentToDelete) {
        String userName = UserCredentials.getCurrentUsername();
        Task task = taskRepository.findByTaskNameAndTaskOwner(taskName, taskOwner);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }

        if (!task.getTaskParticipants().contains(userName)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not a participant of this task.");
        }

        if (!task.getTaskOwner().equals(userName) && !taskCommentToDelete.getUserName().equals(userName)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You can not delete others comment");
        }

        if (task.getTaskComments() == null || !task.getTaskComments().contains(taskCommentToDelete)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found.");
        }

        task.getTaskComments().remove(taskCommentToDelete);
        taskRepository.save(task);
        return ResponseEntity.ok(task);
    }

    // Add a Note to a task
    public ResponseEntity<?> addTaskNote(String taskName,String taskOwner, String noteText) {
        String  userName= UserCredentials.getCurrentUsername();
        Task task = taskRepository.findByTaskNameAndTaskOwner(taskName,taskOwner);
        if(task==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }
        if(task.getTaskParticipants().contains(userName)) {
            task.addTaskNote(userName, noteText);
            taskRepository.save(task);
            return ResponseEntity.ok(task);
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not a participant of this task.");
        }
    }

    // Delete Task Note
    public ResponseEntity<?> deleteTaskNote(String taskName, String taskOwner, Task.Note taskNoteToDelete) {
        String userName = UserCredentials.getCurrentUsername();
        Task task = taskRepository.findByTaskNameAndTaskOwner(taskName, taskOwner);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }

        if (!task.getTaskParticipants().contains(userName)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not a participant of this task.");
        }

        if (task.getTaskNotes() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notes not found.");
        }

        boolean noteFound = false;
        for (List<Task.Note> notes : task.getTaskNotes().values()) {
            if (notes.contains(taskNoteToDelete)) {
                notes.remove(taskNoteToDelete);
                noteFound = true;
                break;
            }
        }

        if (!noteFound) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found.");
        }

        taskRepository.save(task);
        return ResponseEntity.ok(task);
    }

    // Add a Note to a task
    public ResponseEntity<?> addTaskTodo(String taskName, String taskOwner, String todoDescription) {
        String userName = UserCredentials.getCurrentUsername();
        Task task = taskRepository.findByTaskNameAndTaskOwner(taskName, taskOwner);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }

        if (!task.getTaskParticipants().contains(userName)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not a participant of this task.");
        }


        boolean todoExists = task.getTaskTodos().stream()
                .filter(Objects::nonNull)
                .filter(todo -> todo.getDescription() != null)
                .anyMatch(todo -> todo.getDescription().equals(todoDescription));
        if (todoExists) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( "A task todo with this description already exists.");
        }

        int oldProgress = task.getTaskCurrentProgress();
        String description="the task by adding a new Task Todo \" "+todoDescription+" \". Changes: ";
        String oldStatus =" Task Progress: " + oldProgress;


        task.addTaskTodo(userName, todoDescription);

        // Recalculate and update task progress
        int newProgress = calculateTaskProgress(task);
        String newStatus =" Task Progress: " + newProgress;
        task.setTaskCurrentProgress(newProgress); // Update the progress
        task.addTaskTodoModificationHistory(userName,description,oldStatus,newStatus);

        taskRepository.save(task);
        return ResponseEntity.ok(task);
    }
    public ResponseEntity<?> deleteTaskTodo(String taskName, String taskOwner, Task.TaskTodo updatedTaskTodoStatus) {
        String userName = UserCredentials.getCurrentUsername();
        Task task = taskRepository.findByTaskNameAndTaskOwner(taskName, taskOwner);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }

        if (!task.getTaskParticipants().contains(userName)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not a participant of this task.");
        }

        boolean todoExists = task.getTaskTodos().stream()
                .filter(Objects::nonNull)
                .anyMatch(todo -> todo.equals(updatedTaskTodoStatus));

        if (!todoExists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task todo not found.");
        }

        int oldProgress = task.getTaskCurrentProgress();
        String description = "Removed task todo: \"" + updatedTaskTodoStatus.getDescription() + "\". Changes: ";
        String oldStatus = "Task Progress: " + oldProgress;

        task.getTaskTodos().remove(updatedTaskTodoStatus);
        if(task.getTaskTodos().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task todo can not be removed as at least one task todo has to be present");
        }

        // Recalculate and update task progress
        int newProgress = calculateTaskProgress(task);
        String newStatus = "Task Progress: " + newProgress;
        task.setTaskCurrentProgress(newProgress); // Update the progress
        task.addTaskTodoModificationHistory(userName, description, oldStatus, newStatus);

        taskRepository.save(task);
        return ResponseEntity.ok(task);
    }

    // Modify a task attribute
    public ResponseEntity<?> modifyTaskAttribute(TaskDetailsModificationRequestBody requestBody) {
        String updatedBy = UserCredentials.getCurrentUsername();

        if (requestBody == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request body is empty");
        }

        Task task = taskRepository.findByTaskNameAndTaskOwner(requestBody.getPreviousTaskName(), updatedBy);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not the owner of that task.");
        }

        String taskName = requestBody.getTaskName();
        if (taskName != null && !taskName.equals(task.getTaskName()) && !taskName.trim().isEmpty()) {
            Task existingTask = taskRepository.findByTaskNameAndTaskOwner(taskName, updatedBy);
            if (existingTask == null) {

                task.modifyTaskAttribute("taskName", updatedBy, taskName.trim());
                task.setTaskName(taskName.trim());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You already have a task with that task name.");
            }
        }

        String taskCategory = requestBody.getTaskCategory();
        if (taskCategory != null && !taskCategory.equals(task.getTaskCategory()) && !taskCategory.trim().isEmpty()) {

            task.modifyTaskAttribute("taskCategory", updatedBy, taskCategory.trim());
            task.setTaskCategory(taskCategory.trim());
        }

        String taskDescription = requestBody.getTaskDescription();
        if (taskDescription != null && !taskDescription.equals(task.getTaskDescription()) && !taskDescription.isEmpty()) {
            task.setTaskDescription(taskDescription);
            task.modifyTaskAttribute("taskDescription", updatedBy, taskDescription);
            task.setTaskDescription(taskDescription);
        }

        String taskPriority = requestBody.getTaskPriority();
        if (taskPriority != null && !taskPriority.equals(task.getTaskPriority()) && !taskPriority.trim().isEmpty()) {

            task.modifyTaskAttribute("taskPriority", updatedBy, taskPriority);
            task.setTaskPriority(taskPriority);
        }

        String taskVisibilityStatus = requestBody.getTaskVisibilityStatus();
        if (taskVisibilityStatus != null && !taskVisibilityStatus.equals(task.getTaskVisibilityStatus()) && !taskVisibilityStatus.trim().isEmpty()) {

            task.modifyTaskAttribute("taskVisibilityStatus", updatedBy, taskVisibilityStatus);
            task.setTaskVisibilityStatus(taskVisibilityStatus);
        }

        Date taskDeadline = requestBody.getTaskDeadline();
        if (taskDeadline != null) {
            if (taskDeadline.after(task.getTaskCreationDate())) {
                if (!taskDeadline.equals(task.getTaskDeadline())) { // Added check for change
                    task.modifyTaskAttribute("taskDeadline", updatedBy, taskDeadline);
                    task.setTaskDeadline(taskDeadline);
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task deadline must be after creation date.");
            }
        }
        String taskGoal = requestBody.getTaskGoal();
        if (taskGoal != null && !taskGoal.equals(task.getTaskGoal()) && !taskGoal.trim().isEmpty()) {

            task.modifyTaskAttribute("taskGoal", updatedBy, taskGoal);
            task.setTaskGoal(taskGoal);
        }

        taskRepository.save(task);
        return ResponseEntity.ok(task);
    }
    public void dummy(){
//        Set<String> taskParticipants=taskDetailsModificationRequestBody.getTaskParticipants();
//        if(taskParticipants!=null){
//            task.setTaskParticipants(taskParticipants);
//            task.modifyTaskAttribute("taskParticipants",updatedBy, taskParticipants);
//        }
//        Set<String> invitedMembers=taskDetailsModificationRequestBody.getInvitedMembers();
//        if(invitedMembers!=null){
//            task.setInvitedMembers(invitedMembers);
//        }
//        List<Task.Comment> taskComments=taskDetailsModificationRequestBody.getTaskComments();
//        if(taskComments!=null){
//            task.setTaskComments(taskComments);
//        }
//        List<Task.TaskModification> taskModificationHistory=taskDetailsModificationRequestBody.getTaskModificationHistory();
//        if(taskModificationHistory!=null){
//            task.setTaskModificationHistory(taskModificationHistory);
//        }
//        List<Task.TaskTodo> taskTodos=taskDetailsModificationRequestBody.getTaskTodos();
//        if(taskTodos!=null){
//            if(taskTodos.isEmpty()){
//                return null;
//            }
//            task.setTaskTodos(taskTodos);
//            task.modifyTaskAttribute("taskTodos",updatedBy, taskTodos);
//        }
    }

    public ResponseEntity<?> updateTaskTodoStatus(TaskTodoStatusModificationRequestBody requestBody) {
        String userName = UserCredentials.getCurrentUsername();

        // Find task by name and owner
        Optional<Task> taskOptional = Optional.ofNullable(taskRepository.findByTaskNameAndTaskOwner(
                requestBody.getTaskName(), requestBody.getTaskOwner()));

        if (taskOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Task task = taskOptional.get();

        if (!task.getTaskOwner().equals(userName)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Task.TaskTodo updatedTaskTodo = requestBody.getUpdatedTaskTodoStatus();
        if (updatedTaskTodo == null ||
                updatedTaskTodo.getDescription() == null ||
                updatedTaskTodo.getStatus() == null) {
            return ResponseEntity.badRequest().body("Task todo description and status are required.");
        }

        Optional<Task.TaskTodo> existingTodoOptional = task.getTaskTodos().stream()
                .filter(Objects::nonNull) // Filter out null TaskTodo objects
                .filter(todo -> todo.getDescription() != null) // Filter out TaskTodos with null descriptions
                .filter(todo -> todo.getDescription().equals(updatedTaskTodo.getDescription())) // Then compare
                .findFirst();
        if (existingTodoOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Task todo not found.");
        }

        Task.TaskTodo existingTodo = existingTodoOptional.get();

        // Check if status is already what the user is trying to update it to
        if (existingTodo.getStatus().equalsIgnoreCase(updatedTaskTodo.getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The task todo already has this status.");
        }

        String description="the Task Todo \" "+requestBody.getUpdatedTaskTodoStatus().getDescription()+" \" completion status. Changes: ";

        // Capture old status and progress before updating
        int oldProgress = task.getTaskCurrentProgress();
        String oldStatus = "Status: " + existingTodo.getStatus() + ", Task Progress: " + oldProgress;


        existingTodo.setStatus(updatedTaskTodo.getStatus());

        // Calculate new progress
        int newProgress = calculateTaskProgress(task);

        // Set the new progress on the task
        task.setTaskCurrentProgress(newProgress);

        // Create status change string for history with correct status (opposite of the update)
        String newStatus = "Status: " + updatedTaskTodo.getStatus() + ", Task Progress: " + newProgress;
        // Add modification to history
        task.addTaskTodoModificationHistory(userName,description, oldStatus, newStatus);

        // Save and return
        taskRepository.save(task);
        return ResponseEntity.ok(task);
    }


    private int calculateTaskProgress(Task task) {
        int totalTaskTodos = task.getTaskTodos().size();
        if (totalTaskTodos == 0) {
            return 0;
        }

        long completedCount = task.getTaskTodos().stream()
                .filter(todo -> todo.getStatus().equalsIgnoreCase("complete"))
                .count();

        return (int) ((double) completedCount / totalTaskTodos * 100);
    }
    private void updateTaskProgress(Task task) {
        long completedCount = task.getTaskTodos().stream()
                .filter(todo -> todo.getStatus().equalsIgnoreCase("complete"))
                .count();

        int totalTaskTodos = task.getTaskTodos().size();

        if (totalTaskTodos > 0) {
            double newProgress = (double) completedCount / totalTaskTodos; // Calculate as double

            // Store as double (if your Task entity allows it). If it stores int, then:
            int progressPercentage = (int) (newProgress * 100); // Convert to percentage if you need to store it as int
            task.modifyTaskAttribute("taskCurrentProgress", UserCredentials.getCurrentUsername(), progressPercentage);

        } else {
            task.modifyTaskAttribute("taskCurrentProgress", UserCredentials.getCurrentUsername(), 0); // Or 0.0 if the type is double
        }
    }

    // Delete a task by task name
    public ResponseEntity<?> deleteTask(ObjectId taskId) {
        String  userName= UserCredentials.getCurrentUsername();
        Task task=taskRepository.findByTaskIdAndTaskOwner(taskId,userName);
        if(task==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You do not have such task to delete.");
        }
        if(!task.getTaskOwner().equals(userName)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not the owner of this task to delete.");
        }
        taskRepository.deleteByTaskIdAndTaskOwner(taskId,userName);
        return CollaborationEngine.removeDeletedTaskFromTeams(task);
    }


    // Get all tasks summary of a User
    public ResponseEntity<?> getAllTasksSummaryOfAnUser() {
        String userName = UserCredentials.getCurrentUsername();

        List<Task> tasks = taskRepository.findByTaskParticipantsContains(userName);

        if (tasks == null || tasks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You have not participated in any tasks yet.");
        }

        List<TaskSummary> tasksSummary= tasks.stream()
                .map(task -> new TaskSummary(
                        task.getTaskName(),
                        task.getTaskCategory(),
                        task.getTaskCreationDate(),
                        task.getTaskDescription(),
                        task.getTaskGoal(),
                        task.getTaskPriority(),
                        task.getTaskDeadline(),
                        task.getTaskOwner(),
                        task.getTaskCurrentProgress()
                )).sorted((t1, t2) -> {
                    double rank1 = calculateTaskRank(t1);
                    double rank2 = calculateTaskRank(t2);
                    return Double.compare(rank2, rank1); // Descending order (higher rank first)
                }).toList();

        return ResponseEntity.ok(tasksSummary);
    }

    private double calculateTaskRank(TaskSummary taskSummary) {
        // Priority contributes 40%
        double priorityWeight = 0.4;
        int priorityValue = switch (taskSummary.getTaskPriority().toLowerCase()) {
            case "high" -> 3;
            case "medium" -> 2;
            case "low" -> 1;
            default -> 0;
        };

        // Progress contributes 30%
        double progressWeight = 0.3;
        double progressValue = taskSummary.getTaskCurrentProgress() / 100.0; // Assuming progress is a percentage (0-100)

        // Deadline contributes 30%
        double deadlineWeight = 0.3;
        long currentTime = System.currentTimeMillis();
        long deadlineTime = taskSummary.getTaskDeadline().getTime();
        double deadlineValue = deadlineTime > currentTime ? 1.0 - (deadlineTime - currentTime) / (double) (deadlineTime - taskSummary.getTaskCreationDate().getTime()) : 0.0;

        // Calculate total rank
        return (priorityWeight * priorityValue) + (progressWeight * progressValue) + (deadlineWeight * deadlineValue);
    }

    public ResponseEntity<?> getTaskProfile(String taskName,String taskOwner) {
        // Fetch the task from the database
        Task task = taskRepository.findByTaskNameAndTaskOwner(taskName,taskOwner);

        if(task==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }
        if ("private".equalsIgnoreCase(task.getTaskVisibilityStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task is private");
        }


        // Map only the required fields to a new DTO object
        TaskProfile taskProfile = new TaskProfile(
                task.getTaskName(),
                task.getTaskCategory(),
                task.getTaskCreationDate(),
                task.getTaskDescription(),
                task.getTaskGoal()
        );

        return ResponseEntity.ok(taskProfile);

    }


    public ResponseEntity<?> getTaskDetails(String taskName,String taskOwner) {
        String userName = UserCredentials.getCurrentUsername();
        Task task = taskRepository.findByTaskNameAndTaskOwner(taskName,taskOwner);
        if(task==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }
        if(!task.getTaskParticipants().contains(userName)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only task participants can get task details.");
        }
        task.setTaskId(null);
        return ResponseEntity.ok(task);
    }


}
