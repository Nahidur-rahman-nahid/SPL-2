package com.bsse1401_bsse1429.TimeWise.controller;

import com.bsse1401_bsse1429.TimeWise.engine.CollaborationEngine;
import com.bsse1401_bsse1429.TimeWise.model.Notification;
import com.bsse1401_bsse1429.TimeWise.model.Team;
import com.bsse1401_bsse1429.TimeWise.service.TeamService;
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

    // Invite Members to Team
    @PostMapping("/invite")
    public String inviteMembersToTeam(@RequestBody Notification notification,
                                      @RequestParam String from,
                                      @RequestParam String to) {
        return CollaborationEngine.sendEmail(notification);
    }

    // Create Team
    @PostMapping("/create")
    public ResponseEntity<Team> createTeam(@RequestBody Team team) {
        return ResponseEntity.ok(teamService.createTeam(team));
    }

    // Add Task to Team
    @PostMapping("/add/task")
    public ResponseEntity<?> addTaskToTeam(@RequestParam String teamName,
                                           @RequestParam String taskName) {
        return ResponseEntity.ok(teamService.addTaskToTeam(teamName, taskName));
    }

    // Remove Task from Team
    @PostMapping("/remove/task")
    public ResponseEntity<?> removeTaskFromTeam(@RequestParam String teamName,
                                                @RequestParam ObjectId taskId) {
        return ResponseEntity.ok(teamService.removeTaskFromTeam(teamName, taskId));
    }

    // Remove User from Team
    @PostMapping("/remove/user")
    public ResponseEntity<?> removeUserFromTeam(@RequestParam String teamName,
                                                @RequestParam String userName) {
        return ResponseEntity.ok(teamService.removeMemberFromTeam(teamName, userName));
    }

    // Leave Team
    @PostMapping("/leave")
    public ResponseEntity<?> leaveTeam(@RequestParam String teamName) {
        return ResponseEntity.ok(teamService.leaveTeam(teamName));
    }

    // Get Team Details
    @GetMapping("/{teamName}")
    public ResponseEntity<?> getTeamDetails(@PathVariable String teamName) {
        return teamService.getTeamDetails(teamName);
    }

    // Get Teams for a User
    @GetMapping("/user/{userName}")
    public ResponseEntity<List<Team>> getTeamsForUser(@PathVariable String userName) {
        return ResponseEntity.ok(teamService.getTeamsForUser());
    }
}
