package com.TimeWise.repository;

import com.TimeWise.model.Team;
import com.TimeWise.utils.TeamSummary;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends MongoRepository<Team, ObjectId> {
    Team findByTeamName(String teamName);

    List<Team> findByTeamMembersContaining(String userName);

    List<Team> findByTeamOwner(String userName);

    Team findByTeamNameAndTeamOwner(String entityNameRelatedToNotification, String invitedBy);

    List<Team> findByTeamOwnerAndTeamTasksContaining(String teamOwner,String taskName);

    Long countByTeamMembersContains(String userName);

    @Query("{'teamMembers': ?0}")
    List<TeamSummary> findTeamsByUser(String userName);
}
