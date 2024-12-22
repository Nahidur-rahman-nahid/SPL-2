package com.bsse1401_bsse1429.TimeWise.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.TreeMap;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    private ObjectId taskId; // Unique Task ID

    private String taskName; // Task name (may not be unique)

    private String taskCategory; // e.g., "event organizing", "leisure", etc.

    private String taskDescription; // Description (limit enforced at frontend)

    private String taskPriority; // Options: Low, Medium, High

    private Date taskCreationDate; // Date when the task is created

    private Date taskDeadline; // Deadline for task completion

    private String taskOwner; // Task creator/owner's name

    private List<String> taskParticipants; // List of participants' names

    // Task comments - List of Comment objects
    private List<Comment> taskComments;

    // Notes per participant - List of Note objects (for timestamping)
    private List<Note> taskNotes;

    // Progress tracking - Current progress percentage (0 to 100)
    private Integer taskCurrentProgress;

    // Progress history - Ordered: Date | Updated By | Old Progress | New Progress
    private TreeMap<Date, ProgressChange> taskProgressHistory;

    // Modification history - Date | Field Name | Updated By | New Value
    private TreeMap<Date, TaskModification> taskModificationHistory;

    // Method to update progress with validation
    public void updateTaskProgress(String updatedBy, int newProgress) {
        if (newProgress < 0 || newProgress > 100) {
            throw new IllegalArgumentException("Progress must be between 0 and 100.");
        }
        int oldProgress = this.taskCurrentProgress;
        this.taskCurrentProgress = newProgress;

        // Record progress history
        ProgressChange progressChange = new ProgressChange(updatedBy, oldProgress, newProgress);
        this.taskProgressHistory.put(new Date(), progressChange);
    }

    // Method to add a comment
    public void addTaskComment(String username, String commentText) {
        Comment comment = new Comment(new Date(), username, commentText);
        this.taskComments.add(comment);
    }

    // Method to add a note
    public void addTaskNote(String username, String noteText) {
        Note note = new Note(new Date(), username, noteText);
        this.taskNotes.add(note);
    }

    // Modify a task attribute and log changes
    public void modifyTaskAttribute(String fieldName, String updatedBy, String newValue) {
        TaskModification modification = new TaskModification(fieldName, updatedBy, newValue);
        this.taskModificationHistory.put(new Date(), modification);
    }

    // Inner class for Comments
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comment {
        private Date timestamp;
        private String username;
        private String commentText;
    }

    // Inner class for Notes
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Note {
        private Date timestamp;
        private String username;
        private String noteText;
    }

    // Inner class for Progress Changes
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgressChange {
        private String updatedBy;
        private Integer oldProgress;
        private Integer newProgress;
    }

    // Inner class for Task Modifications
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskModification {
        private String fieldName;
        private String updatedBy;
        private String newValue;
    }
}

