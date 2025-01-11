package com.bsse1401_bsse1429.TimeWise.service;

import com.bsse1401_bsse1429.TimeWise.model.Team;
import com.bsse1401_bsse1429.TimeWise.model.Notification;
import com.bsse1401_bsse1429.TimeWise.repository.TeamRepository;
//import com.bsse1401_bsse1429.TimeWise.repository.NotificationRepository;
import com.bsse1401_bsse1429.TimeWise.utils.TeamDetailResponse;
import com.bsse1401_bsse1429.TimeWise.utils.UserCredentials;
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

//    @Autowired
//    private NotificationRepository notificationRepository;

    public Team createTeam(Team team) {
        String  createdBy= UserCredentials.getCurrentUsername();
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



    public List<Team> getTeamsForUser() {
        String  userName=UserCredentials.getCurrentUsername();
        return teamRepository.findByTeamMembersContaining(userName);
    }

    public Team getTeamById(ObjectId teamId) {
        return teamRepository.findById(teamId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found with ID: " + teamId)
        );
    }
    public ResponseEntity<?> getTeamDetails(String teamName) {
        // Fetch the team by teamName
        Team team = teamRepository.findByTeamName(teamName);

        if(team==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team not found");
        }
        if ("private".equalsIgnoreCase(team.getTeamVisibilityStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Team is private");
        }

            // Map only the required fields to a new DTO object
            TeamDetailResponse teamResponse = new TeamDetailResponse(
                    team.getTeamName(),
                    team.getTeamEmail(),
                    team.getTeamDescription(),
                    team.getTeamMembers(),
                    team.getTeamOwner(),
                    team.getCreationDate()

            );

            return ResponseEntity.ok(teamResponse);

    }

}
