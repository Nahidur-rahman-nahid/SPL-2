package com.TimeWise.controller;

import com.TimeWise.engine.CollaborationEngine;
import com.TimeWise.model.Notification;
import com.TimeWise.model.Team;
import com.TimeWise.service.TeamService;
import com.TimeWise.utils.AddTeamChatRequestBody;
import com.TimeWise.utils.AddTeamTaskRequestBody;
import com.TimeWise.utils.RemoveTeamTaskRequestBody;
import com.TimeWise.utils.TeamDetailsModificationRequestBody;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    // Create Team
    @PostMapping("/create")
    public ResponseEntity<?> createTeam(@RequestBody Team team) {
        return teamService.createTeam(team);
    }
    // Update Team
    @PostMapping("/update")
    public ResponseEntity<?> updateTeamDetails(@RequestBody TeamDetailsModificationRequestBody requestBody) {
        return teamService.updateTeamDetails(requestBody);
    }

    // Add Task to Team
    @PostMapping("/task/add")
    public ResponseEntity<?> addTaskToTeam(@RequestBody AddTeamTaskRequestBody requestBody) {
        return teamService.addTaskToTeam(requestBody.getTeamName(), requestBody.getTaskName());
    }

    // Remove Task from Team
    @DeleteMapping("/task/remove")
    public ResponseEntity<?> removeTaskFromTeam(@RequestBody RemoveTeamTaskRequestBody requestBody) {
        return teamService.removeTaskFromTeam(requestBody.getTeamName(), requestBody.getTaskName());
    }



    // Invite Members to Team
    @PostMapping("/user/invite")
    public ResponseEntity<?> inviteMembers(
                                                 @RequestParam String teamName,
                                                 @RequestParam String recipient) {
        return teamService.inviteMembers(teamName,recipient);
    }

    @PutMapping("/user/invite/response")
    public ResponseEntity<?> handleInvitationResponse(
            @RequestParam String teamName,
            @RequestParam String response) {
        return teamService.handleInvitationResponse(teamName,response);
    }
    // Invite Members to Team
    @PostMapping("/user/join/request")
    public ResponseEntity<?> requestTeamJoining(
            @RequestParam String teamName) {
        return teamService.requestTeamJoining(teamName);
    }

    @PutMapping("/user/join/request/response")
    public ResponseEntity<?> handleTeamJoiningRequestResponse(
            @RequestParam String teamName,
            @RequestParam String respondedTo,
           @RequestParam String response) {
        return teamService.handleTeamJoiningRequestResponse(teamName,respondedTo,response);
    }

    // Remove Members from Team
    @DeleteMapping("/user/remove")
    public ResponseEntity<?> removeUserFromTeam(@RequestParam String teamName,
                                                @RequestParam String userName) {
        return ResponseEntity.ok(teamService.removeMemberFromTeam(teamName, userName));
    }

    // Leave Team
    @PostMapping("/leave")
    public ResponseEntity<?> leaveTeam(@RequestParam String teamName) {
        return ResponseEntity.ok(teamService.leaveTeam(teamName));
    }

    // Get a tasks details
    @GetMapping("/details")
    public ResponseEntity<?> getTeamDetails(@RequestParam String teamName) {

        return teamService.getTeamDetails(teamName);

    }

    // Get Team Details
    @GetMapping("/profile")
    public ResponseEntity<?> getTeamProfile(@RequestParam String teamName) {
        return teamService.getTeamProfile(teamName);
    }

    // Get Teams for a User
    @GetMapping("/user")
    public ResponseEntity<?> getTeamsForUser() {
        return teamService.getTeamsForUser();
    }

    @PostMapping("/chat/add")
    public ResponseEntity<?> addTeamChat(@RequestBody AddTeamChatRequestBody addTeamChatRequestBody) {
        return teamService.addTeamChat(addTeamChatRequestBody.getTeamName(),addTeamChatRequestBody.getChat());
    }
    @DeleteMapping("/chat/remove")
    public ResponseEntity<?> removeTeamChat(@RequestParam String teamName,@RequestBody Team.Chat teamChat) {
        return teamService.removeTeamChat(teamName,teamChat);
    }
}
