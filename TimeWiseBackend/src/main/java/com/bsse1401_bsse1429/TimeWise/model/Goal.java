package com.bsse1401_bsse1429.TimeWise.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goal {
    @Id
    private ObjectId goalId;
    private String userName;
    private String goalName;
    private String goalCategory; // e.g., Fitness, Education, Career, etc.

    private String goalDescription; // Detailed description of the goal

    private Date goalDeadline; // Deadline for goal completion

    private Date goalCreationDate; // When the goal was created
    private List<Note> goalNotes;
    private String goalCompletionStatus; // "In Progress", "Completed", etc.
    private String goalVisibilityStatus; // public or private
    private List<Comment> goalComments; // Comments on the goal

    private List<ObjectId> goalTasks; // Tasks that are part of this goal

    private Double goalProgress; // Overall progress percentage (0-100)

    // Method to calculate and update goal progress based on tasks
//    public void updateGoalProgress() {
//        if (goalTasks == null || goalTasks.isEmpty()) {
//            this.goalProgress = 0;
//            this.goalCompletionStatus = "Not Started";
//            return;
//        }
//
//        int totalProgress = 0;
//        int completedTasks = 0;
//
//        for (Task task : goalTasks) {
//            totalProgress += task.getTaskCurrentProgress();
//            if (task.getTaskCurrentProgress() == 100) {
//                completedTasks++;
//            }
//        }
//
//        this.goalProgress = totalProgress / goalTasks.size();
//
//        // Update completion status
//        if (completedTasks == goalTasks.size()) {
//            this.goalCompletionStatus = "Completed";
//        } else {
//            this.goalCompletionStatus = "In Progress";
//        }
//    }

    // Method to add a comment to the goal
    public void addGoalComment(String username, String commentText) {
        if (this.goalComments == null) {
            this.goalComments = new java.util.ArrayList<>();
        }
        this.goalComments.add(new Comment(new Date(), username, commentText));
    }

    // Inner class for Comments
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comment {
        private Date timestamp; // Date and time of the comment
        private String username; // User who added the comment
        private String commentText; // The comment content
    }
    // Inner class for Notes
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Note {
        private Date timestamp;
        private String noteText;
    }

}
