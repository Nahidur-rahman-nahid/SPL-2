package com.bsse1401_bsse1429.TimeWise.repository;

import com.bsse1401_bsse1429.TimeWise.model.Session;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SessionRepository extends MongoRepository<Session, ObjectId> {
    Session findBySessionId(String id);

    List<Session> findBySessionCreator(String creator);
}
