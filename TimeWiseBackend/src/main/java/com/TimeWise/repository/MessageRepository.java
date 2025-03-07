package com.TimeWise.repository;

import com.TimeWise.model.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface MessageRepository extends MongoRepository<Message, ObjectId> {
    Long countBySender(String userName);

    Long countByRecipientsContains(String userName);

    Long countBySenderAndTimeStampAfter(String userName, Date cutoffDate);

    Long countByRecipientsContainsAndTimeStampAfter(String userName, Date cutoffDate);

    List<Message> findBySenderOrRecipientsContains(String currentUser, String user);

    List<Message> findByRecipientsContains(String currentUser);
}

