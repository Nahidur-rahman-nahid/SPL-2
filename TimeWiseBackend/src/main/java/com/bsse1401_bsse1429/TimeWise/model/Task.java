package com.bsse1401_bsse1429.TimeWise.model;

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
    private Set<String> taskParticipants; // Letter on we may add task admins assigned by the task owner
    private List<Comment> taskComments;
    private TreeMap<String, List<Note>> taskNotes;
    private Integer taskCurrentProgress;
    private List<TaskModification> taskModificationHistory;

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

    public void modifyTaskAttribute(String fieldName, String updatedBy, Object newValue) {
        if (fieldName == null || updatedBy == null || newValue == null) {
            throw new IllegalArgumentException("Field name, updatedBy, and newValue cannot be null.");
        }

        Object previousValue;
        switch (fieldName) {
            case "taskCurrentProgress":
                if (newValue instanceof String) {
                    try {
                        // Try to parse the string to an integer
                        int progress = Integer.parseInt((String) newValue);
                        if (progress < 0 || progress > 100) {
                            throw new IllegalArgumentException("taskCurrentProgress must be an Integer between 0 and 100.");
                        }
                        previousValue = this.taskCurrentProgress;
                        this.taskCurrentProgress = progress;
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("taskCurrentProgress must be a valid integer.");
                    }
                } else if (newValue instanceof Integer) {
                    int progress = (Integer) newValue;
                    if (progress < 0 || progress > 100) {
                        throw new IllegalArgumentException("taskCurrentProgress must be an Integer between 0 and 100.");
                    }
                    previousValue = this.taskCurrentProgress;
                    this.taskCurrentProgress = progress;
                } else {
                    throw new IllegalArgumentException("taskCurrentProgress must be a valid integer or string representing an integer.");
                }
                break;
            case "taskName":
                previousValue = this.taskName;
                this.taskName = (String) newValue;
                break;
            case "taskCategory":
                previousValue = this.taskCategory;
                this.taskCategory = (String) newValue;
                break;
            case "taskDescription":
                previousValue = this.taskDescription;
                this.taskDescription = (String) newValue;
                break;
            case "taskPriority":
                previousValue = this.taskPriority;
                this.taskPriority = (String) newValue;
                break;
            case "taskVisibilityStatus":
                previousValue = this.taskVisibilityStatus;
                this.taskVisibilityStatus = (String) newValue;
                break;
            case "taskGoal":
                previousValue = this.taskGoal;
                this.taskGoal = (String) newValue;
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
                break;
            default:
                throw new IllegalArgumentException("Field name not recognized for modification.");
        }

        // Log the modification
        TaskModification modification = new TaskModification(new Date(), fieldName, updatedBy, previousValue, this.taskCurrentProgress);
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

}
