package com.bsse1401_bsse1429.TimeWise.service;

import com.bsse1401_bsse1429.TimeWise.engine.CollaborationEngine;
import com.bsse1401_bsse1429.TimeWise.model.Team;
import com.bsse1401_bsse1429.TimeWise.repository.TeamRepository;
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


    public Team createTeam(Team team) {
        String  createdBy= UserCredentials.getCurrentUsername();
        if (team.getTeamName() == null || teamRepository.findByTeamName(team.getTeamName()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team name is required and must be unique.");
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

        return teamRepository.save(team);
    }

    public List<Team> getTeamsForUser() {
        String  userName=UserCredentials.getCurrentUsername();
        return teamRepository.findByTeamMembersContaining(userName);
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
                    team.getTeamDescription(),
                    team.getTeamMembers(),
                    team.getTeamOwner(),
                    team.getCreationDate()

            );

            return ResponseEntity.ok(teamResponse);

    }

    public String addTaskToTeam(String teamName, String taskName) {
        String  userName=UserCredentials.getCurrentUsername();
       return CollaborationEngine.addTaskToTeam(userName,teamName,taskName);

    }

    public String removeTaskFromTeam(String teamName, ObjectId taskId) {
        String  userName=UserCredentials.getCurrentUsername();
        return CollaborationEngine.removeTaskFromTeam(userName,teamName,taskId);
    }

    public Object removeMemberFromTeam(String teamName, String userName) {
        String  teamOwner=UserCredentials.getCurrentUsername();
        return CollaborationEngine.removeMemberFromTeam(teamOwner,teamName,userName);
    }

    public String leaveTeam(String teamName) {
        String  userName=UserCredentials.getCurrentUsername();
        return CollaborationEngine.leaveTeam(teamName,userName);

    }
}
