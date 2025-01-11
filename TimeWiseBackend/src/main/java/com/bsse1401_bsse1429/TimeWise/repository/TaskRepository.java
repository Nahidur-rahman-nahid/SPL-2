package com.bsse1401_bsse1429.TimeWise.repository;

import com.bsse1401_bsse1429.TimeWise.model.Task;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<Task, ObjectId> {
    List<Task> findByTaskParticipantsContains(String userName);

    Task findByTaskNameAndTaskOwner(String taskName,String userName);

    Task findByTaskOwnerAndTaskName(String userName, String taskName);

    Task findByTaskId(ObjectId taskId);
}
