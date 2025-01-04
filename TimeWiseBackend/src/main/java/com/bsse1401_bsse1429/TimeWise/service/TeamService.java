package com.bsse1401_bsse1429.TimeWise.service;

import com.bsse1401_bsse1429.TimeWise.model.Team;
import com.bsse1401_bsse1429.TimeWise.model.Notification;
import com.bsse1401_bsse1429.TimeWise.repository.TeamRepository;
//import com.bsse1401_bsse1429.TimeWise.repository.NotificationRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

//    @Autowired
//    private NotificationRepository notificationRepository;

    public Team createTeam(Team team, String createdBy) {
        if (team.getTeamName() == null || teamRepository.findByTeamName(team.getTeamName()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team name is required and must be unique.");
        }

        team.setTeamOwner(createdBy);

        if (team.getTeamDescription() == null) {
            team.setTeamDescription("This is a team created by " + createdBy);
        }

        if (team.getTeamMembers() == null) {
            team.setTeamMembers(new HashSet<>());
        }
        team.getTeamMembers().add(createdBy);

        if (team.getCreationDate() == null) {
            team.setCreationDate(new Date());
        }

        if (team.getTeamGoals() == null) {
            team.setTeamGoals(new ArrayList<>());
        }

        if (team.getTeamVisibilityStatus() == null || team.getTeamVisibilityStatus().isEmpty()) {
            team.setTeamVisibilityStatus("Private");
        }

        if (team.getTeamChat() == null) {
            team.setTeamChat(new ArrayList<>());
        }

        if (team.getTeamModificationHistory() == null) {
            team.setTeamModificationHistory(new ArrayList<>());
        }

        return teamRepository.save(team);
    }

//    public void sendJoinRequest(ObjectId teamId, String userName, String requestedBy) {
//        Team team = getTeamById(teamId);
//
//        if (!team.getTeamMembers().contains(requestedBy)) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only team members can invite others.");
//        }
//
//        Notification notification = new Notification();
//        notification.setRecipient(userName);
//        notification.setSender(requestedBy);
//        notification.setMessage("You have been invited to join the team: " + team.getTeamName());
//        notification.setTeamId(teamId);
//        notification.setType("Team Join Request");
//        notification.setStatus("Pending");
//
//       // notificationRepository.save(notification);
//    }

    public Team handleJoinRequest(ObjectId teamId, String userName, boolean accept) {
        Team team = getTeamById(teamId);

        if (!accept) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User declined the invitation.");
        }

        if (team.getTeamMembers().contains(userName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already a member.");
        }

        team.getTeamMembers().add(userName);
        return teamRepository.save(team);
    }

    public List<Team> getTeamsForUser(String userName) {
        return teamRepository.findByTeamMembersContaining(userName);
    }

    public Team getTeamById(ObjectId teamId) {
        return teamRepository.findById(teamId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found with ID: " + teamId)
        );
    }
}
