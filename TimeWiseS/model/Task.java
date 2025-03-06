package com.TimeWise.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    private ObjectId taskId;
    private String taskName;
    private String taskCategory;
    private String taskDescription;
    private String taskPriority;
    private String taskVisibilityStatus;
    private Date taskCreationDate;
    private Date taskDeadline;
    private String taskOwner;
    private String taskGoal;
    private Set<String> taskParticipants;
    private Set<String> invitedMembers;
    private List<Comment> taskComments;
    private TreeMap<String, List<Note>> taskNotes;
    private Integer taskCurrentProgress;
    private List<TaskModification> taskModificationHistory;
    private List<TaskTodo> taskTodos;



    // Method to update progress with validation, now using modifyTaskAttribute
    public void updateTaskProgress(String updatedBy, Object newProgress) {
        this.modifyTaskAttribute("taskCurrentProgress", updatedBy, newProgress);
    }

    // Method to add a comment
    public void addTaskComment(String userName, String commentText) {
        Comment comment = new Comment(new Date(), userName, commentText);
        this.taskComments.add(comment);
    }

    // Method to add a note
    public void addTaskNote(String userName, String noteText) {
        Note note = new Note(new Date(), noteText);
        this.taskNotes.computeIfAbsent(userName, k -> new ArrayList<>()).add(note);
    }

    public void addTaskTodo(String userName, String description) {
        TaskTodo taskTodo = new TaskTodo(new Date(), description,"Incomplete");
        this.taskTodos.add(taskTodo);
    }

    public void modifyTaskAttribute(String fieldName, String updatedBy, Object newValue) {
        Object previousValue;
        String entityChanged;
        switch (fieldName) {
            case "taskName":
                previousValue = this.taskName;
                this.taskName = (String) newValue;
                entityChanged="Task Name";
                break;
            case "taskCategory":
                previousValue = this.taskCategory;
                this.taskCategory = (String) newValue;
                entityChanged="Task Category";
                break;
            case "taskDescription":
                previousValue = this.taskDescription;
                this.taskDescription = (String) newValue;
                entityChanged="Task Description";
                break;
            case "taskPriority":
                previousValue = this.taskPriority;
                this.taskPriority = (String) newValue;
                entityChanged="Task Priority";
                break;
            case "taskVisibilityStatus":
                previousValue = this.taskVisibilityStatus;
                this.taskVisibilityStatus = (String) newValue;
                entityChanged="Task Visibility Status";
                break;
            case "taskGoal":
                previousValue = this.taskGoal;
                this.taskGoal = (String) newValue;
                entityChanged="Task Goal";
                break;
            case "taskDeadline":
                if (newValue instanceof String) {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                        previousValue = this.taskDeadline;
                        this.taskDeadline = dateFormat.parse((String) newValue); // Convert string to Date
                    } catch (ParseException e) {
                        throw new IllegalArgumentException("taskDeadline must be a valid date string.");
                    }
                } else if (newValue instanceof Date) {
                    previousValue = this.taskDeadline;
                    this.taskDeadline = (Date) newValue; // Already a Date, no conversion needed
                } else {
                    throw new IllegalArgumentException("taskDeadline must be a Date or a valid date string.");
                }
                entityChanged="Task Deadline";
                break;
            default:
                throw new IllegalArgumentException("Field name not recognized for modification.");
        }

        // Log the modification
        TaskModification modification = new TaskModification(new Date(), entityChanged, updatedBy, previousValue, newValue);
        this.taskModificationHistory.add(modification);
    }

    public void addTaskTodoModificationHistory(String updatedBy,String changeDescription,String oldStatus,String currentStatus){
        TaskModification modification = new TaskModification(new Date(), changeDescription, updatedBy, oldStatus, currentStatus);
        this.taskModificationHistory.add(modification);

    }



    // Inner class for Comments
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comment {
        private Date timestamp;
        private String userName;
        private String commentText;
    }

    // Inner class for Notes
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Note {
        private Date timestamp;
        private String noteText;
    }

    // Inner class for Task Modifications
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskModification {
        private Date timestamp;
        private String fieldName;
        private String updatedBy;
        private Object previousValue;
        private Object newValue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskTodo {
        private Date timestamp;
        private String description;
        private String status; // complete or incomplete
    }

}
