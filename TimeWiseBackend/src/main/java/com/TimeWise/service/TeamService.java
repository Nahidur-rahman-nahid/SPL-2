package com.TimeWise.service;

import com.TimeWise.engine.CollaborationEngine;
import com.TimeWise.model.Task;
import com.TimeWise.model.Team;
import com.TimeWise.repository.TeamRepository;
import com.TimeWise.utils.TeamDetailResponse;
import com.TimeWise.utils.TeamDetailsModificationRequestBody;
import com.TimeWise.utils.TeamSummary;
import com.TimeWise.utils.UserCredentials;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;


    public ResponseEntity<?> createTeam(Team team) {
        String createdBy = UserCredentials.getCurrentUsername();
        if (team.getTeamName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Team name is required.");
        }
        if (teamRepository.findByTeamName(team.getTeamName()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Team name already exist and it must be unique.");
        }

        team.setTeamOwner(createdBy);

        if (team.getTeamDescription() == null) {
            team.setTeamDescription("This is a team created by " + createdBy);
        }
        team.setTeamMembers(new HashSet<>());
        team.getTeamMembers().add(createdBy);
        team.setInvitedMembers(new HashSet<>());
        team.setRequestedToJoinMembers(new HashSet<>());
        team.setCreationDate(new Date());

        if (team.getTeamVisibilityStatus() == null || team.getTeamVisibilityStatus().isEmpty()) {
            team.setTeamVisibilityStatus("Public");
        }
        team.setTeamModificationHistories(new ArrayList<>());
        team.setTeamChat(new ArrayList<>());
        team.setTeamTasks(new HashSet<>());

        teamRepository.save(team);
        return ResponseEntity.ok(team);
    }

    public ResponseEntity<?> getTeamsForUser() {
        String userName = UserCredentials.getCurrentUsername();
        List<TeamSummary> teams = teamRepository.findTeamsByUser(userName);

        if (teams.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You have not participated in any teams yet.");
        }

        return ResponseEntity.ok(teams);
    }


    public ResponseEntity<?> getTeamProfile(String teamName) {
        // Fetch the team by teamName
        Team team = teamRepository.findByTeamName(teamName);

        if (team == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team not found");
        }
        if ("private".equalsIgnoreCase(team.getTeamVisibilityStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Team is private");
        }

        // Map only the required fields to a new DTO object
        TeamDetailResponse teamResponse = new TeamDetailResponse(
                team.getTeamName(),
                team.getTeamDescription(),
                team.getTeamMembers(),
                team.getTeamOwner(),
                team.getCreationDate()

        );

        return ResponseEntity.ok(teamResponse);

    }

    public ResponseEntity<?> getTeamDetails(String teamName) {
        String userName = UserCredentials.getCurrentUsername();
        Team team = teamRepository.findByTeamName(teamName);

        if (team == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team not found");
        }
        if (!team.getTeamMembers().contains(userName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only team members can get team details.");
        }
        team.setTeamId(null);
        return ResponseEntity.ok(team);

    }


    public ResponseEntity<?> addTaskToTeam(String teamName, String taskName) {
        String userName = UserCredentials.getCurrentUsername();
        return CollaborationEngine.addTaskToTeam(userName, teamName, taskName);

    }

    public ResponseEntity<?> removeTaskFromTeam(String teamName, String taskName) {
        String userName = UserCredentials.getCurrentUsername();
        return CollaborationEngine.removeTaskFromTeam(userName, teamName, taskName);
    }

    public ResponseEntity<?> removeMemberFromTeam(String teamName, String userName) {
        String teamOwner = UserCredentials.getCurrentUsername();
        return CollaborationEngine.removeMemberFromTeam(teamOwner, teamName, userName);
    }

    public ResponseEntity<?> leaveTeam(String teamName) {
        String userName = UserCredentials.getCurrentUsername();
        return CollaborationEngine.leaveTeam(teamName, userName);

    }

    public ResponseEntity<?> addTeamChat(String teamName, String chat) {
        String userName = UserCredentials.getCurrentUsername();
        Team team = teamRepository.findByTeamName(teamName);
        if (team == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team not found");
        }
        if (!team.getTeamMembers().contains(userName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only team members can add team chat.");
        }
        team.addTeamChat(userName, chat);
        teamRepository.save(team);
        return ResponseEntity.ok(team);
    }

    public ResponseEntity<?> removeTeamChat(String teamName, Team.Chat teamChat) {
        String userName = UserCredentials.getCurrentUsername();
        Team team = teamRepository.findByTeamName(teamName);

        if (team == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team not found");
        }

        if (!team.getTeamMembers().contains(userName)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not a participant of this team.");
        }

        if (!team.getTeamOwner().equals(userName) && !teamChat.getSender().equals(userName)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You can not delete others chat");
        }

        if (team.getTeamChat() == null || !team.getTeamChat().contains(teamChat)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat not found.");
        }

        team.getTeamChat().remove(teamChat);
        teamRepository.save(team);
        return ResponseEntity.ok(team);
    }

    public ResponseEntity<?> inviteMembers(String teamName, String recipient) {
        String sender = UserCredentials.getCurrentUsername();
        return CollaborationEngine.handleTeamJoiningInvitation(teamName, sender, recipient);
    }

    public ResponseEntity<?> handleInvitationResponse(String teamName, String response) {
        String respondedBy = UserCredentials.getCurrentUsername();
        return CollaborationEngine.handleTeamJoiningInvitationResponse(teamName, respondedBy, response);
    }

    public ResponseEntity<?> updateTeamDetails(TeamDetailsModificationRequestBody requestBody) {
        String updatedBy = UserCredentials.getCurrentUsername();

        if (requestBody == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request body is empty");
        }

        // Using currentTeamName from your frontend code
        String currentTeamName = requestBody.getTeamName();
        if (currentTeamName == null || currentTeamName.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Team name is required");
        }

        String previousTeamName = requestBody.getPreviousTeamName();
        // Find the team by current name and owner
        Team team = teamRepository.findByTeamNameAndTeamOwner(previousTeamName, updatedBy);

        if (team == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not the owner of that team.");
        }

        // Update team name if provided and changed
        String teamName = requestBody.getTeamName();
        if (teamName != null && !teamName.equals(team.getTeamName()) && !teamName.trim().isEmpty()) {
            Team existingTeam = teamRepository.findByTeamNameAndTeamOwner(teamName, updatedBy);
            if (existingTeam == null) {
                team.updateTeamDetails(updatedBy, "teamName", teamName);
                team.setTeamName(teamName.trim());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You already have a team with that team name.");
            }
        }

        // Update team description if provided and changed
        String teamDescription = requestBody.getTeamDescription();
        if (teamDescription != null && !teamDescription.equals(team.getTeamDescription())) {
            team.updateTeamDetails(updatedBy, "teamDescription", teamDescription);
            team.setTeamDescription(teamDescription);
        }

        // Update team visibility status if provided and changed
        String teamVisibilityStatus = requestBody.getTeamVisibilityStatus();
        if (teamVisibilityStatus != null && !teamVisibilityStatus.equals(team.getTeamVisibilityStatus()) && !teamVisibilityStatus.trim().isEmpty()) {
            team.updateTeamDetails(updatedBy, "teamVisibilityStatus", teamVisibilityStatus);
            team.setTeamVisibilityStatus(teamVisibilityStatus);
        }

        teamRepository.save(team);
        return ResponseEntity.ok(team);
    }


    public ResponseEntity<?> requestTeamJoining(String teamName) {
        String sender = UserCredentials.getCurrentUsername();
        return CollaborationEngine.handleTeamJoiningRequest(sender, teamName);

    }

    public ResponseEntity<?> handleTeamJoiningRequestResponse(String teamName, String respondedTo, String response) {
        String respondedBy = UserCredentials.getCurrentUsername();
        return CollaborationEngine.handleTeamJoiningRequestResponse(respondedBy, teamName, respondedTo, response);
    }

}
