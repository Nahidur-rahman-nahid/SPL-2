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
    private CollaborationEngine collaborationEngine;

    @Autowired
    private TeamService teamService;

    @PostMapping("/invite")
    public String inviteMembersToTeam(@RequestBody Notification notification, @RequestParam String from, @RequestParam String to) {
        //return ResponseEntity.ok(teamService.createTeam(team, createdBy));
        return collaborationEngine.sendEmail(notification,from,to);
    }

    @PostMapping("/create")
    public ResponseEntity<Team> createTeam(@RequestBody Team team, @RequestParam String createdBy) {
        return ResponseEntity.ok(teamService.createTeam(team, createdBy));
    }

//    @PostMapping("/{teamId}/request-join")
//    public ResponseEntity<Void> sendJoinRequest(
//            @PathVariable ObjectId teamId,
//            @RequestParam String userName,
//            @RequestParam String requestedBy
//    ) {
//        teamService.sendJoinRequest(teamId, userName, requestedBy);
//        return ResponseEntity.ok().build();
//    }

    @PostMapping("/{teamId}/handle-join")
    public ResponseEntity<Team> handleJoinRequest(
            @PathVariable ObjectId teamId,
            @RequestParam String userName,
            @RequestParam boolean accept
    ) {
        return ResponseEntity.ok(teamService.handleJoinRequest(teamId, userName, accept));
    }

    @GetMapping("/{userName}/my-teams")
    public ResponseEntity<List<Team>> getTeamsForUser(@PathVariable String userName) {
        return ResponseEntity.ok(teamService.getTeamsForUser(userName));
    }
}
