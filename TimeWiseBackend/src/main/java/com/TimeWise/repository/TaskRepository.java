package com.TimeWise.repository;

import com.TimeWise.model.Task;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TaskRepository extends MongoRepository<Task, ObjectId> {
    List<Task> findByTaskParticipantsContains(String userName);

    Task findByTaskNameAndTaskOwner(String taskName,String userName);


    Task findByTaskId(ObjectId taskId);

    Task findByTaskOwnerAndTaskNameAndTaskGoal(String userName, String taskName, String taskGoal);

    List<Task> findByTaskIdIn(List<ObjectId> taskIds);

    Task findByTaskIdAndTaskOwner(ObjectId taskId, String entityOwner);

    List<Task> findByTaskOwner(String userName);

    @Query("SELECT t.taskName FROM Task t WHERE t.taskOwner = :taskOwner")
    List<String> findTaskNamesByTaskOwner(@Param("taskOwner") String taskOwner);

    Task findByTaskNameAndTaskOwnerAndInvitedMembersContains(String entityName, String entityOwner, String respondedBy);

    List<Task> findByTaskOwnerAndTaskNameIn(String teamOwner, Set<String> teamTasks);

    void deleteByTaskOwnerAndTaskName(String userName, String taskName);

    Long countByTaskParticipantsContainsAndTaskCurrentProgress(String userName, int i);

    Long countByTaskParticipantsContains(String userName);
}
