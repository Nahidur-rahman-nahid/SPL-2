package com.TimeWise.repository;

import com.TimeWise.model.Session;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SessionRepository extends MongoRepository<Session, ObjectId> {
    Session findBySessionId(String id);

    List<Session> findBySessionCreator(String creator);

    Long countBySessionCreator(String userName);
}
