package com.bsse1401_bsse1429.TimeWise.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    @Id
    private ObjectId teamId;
    private String teamName;
    private String teamDescription;
    private Set<String> teamMembers; // Members in the team
    private Set<String> invitedMembers; // Members invited but not yet joined
    private Set<String> requestedToJoinMembers;
    private String teamOwner; // Owner of the team
    private Date creationDate; // Team creation timestamp
    private String teamVisibilityStatus; // e.g., Public or Private
    private List<String> teamModificationHistories; // Simplified modification history as descriptive strings
    private List<Chat> teamChat; // Team chat history
    private Set<String> teamTasks; // List of team tasks

    // Add a member to the team
    public void addTeamMember(String addedBy, String newMember) {
        if (!teamOwner.equals(addedBy)) {
            throw new IllegalArgumentException("Only the team owner can add members.");
        }
        if (teamMembers == null) {
            teamMembers = new HashSet<>();
        }
        teamMembers.add(newMember);
        logModification("Member added: " + newMember + " by " + addedBy);
    }

    // Remove a member from the team
    public void removeTeamMember(String removedBy, String memberToRemove) {
        if (!teamOwner.equals(removedBy)) {
            throw new IllegalArgumentException("Only the team owner can remove members.");
        }
        if (teamMembers != null && teamMembers.contains(memberToRemove)) {
            teamMembers.remove(memberToRemove);
            logModification("Member removed: " + memberToRemove + " by " + removedBy);
        } else {
            throw new IllegalArgumentException("Member not found in the team.");
        }
    }

    // Update team details (name, description, visibility)
    public void updateTeamDetails(String updatedBy, String fieldName, Object newValue) {
        if (!teamOwner.equals(updatedBy)) {
            throw new IllegalArgumentException("Only the team owner can update team details.");
        }
        Object previousValue;
        switch (fieldName) {
            case "teamName":
                previousValue = this.teamName;
                this.teamName = (String) newValue;
                break;
            case "teamDescription":
                previousValue = this.teamDescription;
                this.teamDescription = (String) newValue;
                break;
            case "teamVisibilityStatus":
                previousValue = this.teamVisibilityStatus;
                this.teamVisibilityStatus = (String) newValue;
                break;
            default:
                throw new IllegalArgumentException("Field name not recognized for modification.");
        }
        logModification("Updated " + fieldName + " from " + previousValue + " to " + newValue + " by " + updatedBy);
    }

    // Add a chat message (any member can add a chat)
    public void addTeamChat(String sender, String message) {
        if (teamMembers == null || !teamMembers.contains(sender)) {
            throw new IllegalArgumentException("Only team members can send messages.");
        }
        if (teamChat == null) {
            teamChat = new ArrayList<>();
        }
        teamChat.add(new Chat(new Date(), sender, message));
    }

    // Add a task to the team
    public void addTeamTask(String addedBy, String taskName) {
        if (!teamOwner.equals(addedBy)) {
            throw new IllegalArgumentException("Only the team owner can add tasks.");
        }
        if (teamTasks == null) {
            teamTasks = new HashSet<>();
        }
        teamTasks.add(taskName);
        logModification("Task added: " + taskName + " by " + addedBy);
    }

    // Log any modification to the team
    private void logModification(String description) {
        if (teamModificationHistories == null) {
            teamModificationHistories = new ArrayList<>();
        }
        teamModificationHistories.add(new Date() + " - " + description);
    }

    // Inner class for chat
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Chat {
        private Date timestamp;
        private String sender;
        private String message;
    }
}
