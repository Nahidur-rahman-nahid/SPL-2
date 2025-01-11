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
    private String teamEmail;
    private String teamDescription;
    private Set<String> teamMembers; // Letter on we may add team admins assigned by the team owner
    private Set<String> invitedMembers;
    private Set<String> membersRequestedForJoining;
    private String teamOwner;
    private Date creationDate;
    private List<String> teamGoals;
    private String teamVisibilityStatus;
    private List<Chat> teamChat;
    private List<TeamModification> teamModificationHistory;

    // Add a new member to the team
    public void addTeamMember(String memberName, String updatedBy) {
        if (this.teamMembers.contains(memberName)) {
            throw new IllegalArgumentException("Member already exists in the team.");
        }
        this.teamMembers.add(memberName);
        this.logTeamModification("teamMembers", updatedBy, null, memberName);
    }

    // Remove a member from the team
    public void removeTeamMember(String memberName, String updatedBy) {
        if (!this.teamMembers.contains(memberName)) {
            throw new IllegalArgumentException("Member does not exist in the team.");
        }
        this.teamMembers.remove(memberName);
        this.logTeamModification("teamMembers", updatedBy, memberName, null);
    }

    // Add a goal to the team
    public void addTeamGoal(String goal, String updatedBy) {
        this.teamGoals.add(goal);
        this.logTeamModification("teamGoals", updatedBy, null, goal);
    }

    // Remove a goal from the team
    public void removeTeamGoal(String goal, String updatedBy) {
        if (!this.teamGoals.contains(goal)) {
            throw new IllegalArgumentException("Goal does not exist in the team.");
        }
        this.teamGoals.remove(goal);
        this.logTeamModification("teamGoals", updatedBy, goal, null);
    }

    // Add a chat message
    public void addChatMessage(String sender, String message) {
        Chat chat = new Chat(new Date(), sender, message);
        this.teamChat.add(chat);
    }

    // Update team visibility
    public void updateVisibilityStatus(String updatedBy, String newVisibilityStatus) {
        String previousValue = this.teamVisibilityStatus;
        this.teamVisibilityStatus = newVisibilityStatus;
        this.logTeamModification("teamVisibilityStatus", updatedBy, previousValue, newVisibilityStatus);
    }

    // Modify team attributes (generic)
    public void modifyTeamAttribute(String fieldName, String updatedBy, Object newValue) {
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
            case "teamOwner":
                previousValue = this.teamOwner;
                this.teamOwner = (String) newValue;
                break;
            default:
                throw new IllegalArgumentException("Field name not recognized for modification.");
        }
        this.logTeamModification(fieldName, updatedBy, previousValue, newValue);
    }

    // Log team modification
    private void logTeamModification(String fieldName, String updatedBy, Object previousValue, Object newValue) {
        TeamModification modification = new TeamModification(new Date(), fieldName, updatedBy, previousValue, newValue);
        this.teamModificationHistory.add(modification);
    }

    // Inner class for Chat messages
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Chat {
        private Date timestamp;
        private String sender;
        private String message;
    }

    // Inner class for Team Modifications
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamModification {
        private Date timestamp;
        private String fieldName;
        private String updatedBy;
        private Object previousValue;
        private Object newValue;
    }
}
