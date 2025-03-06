package com.TimeWise.repository;

import com.TimeWise.model.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;

public interface MessageRepository extends MongoRepository<Message, ObjectId> {
    Long countBySender(String userName);

    Long countByRecipientsContains(String userName);

    Long countBySenderAndTimeStampAfter(String userName, Date cutoffDate);

    Long countByRecipientsContainsAndTimeStampAfter(String userName, Date cutoffDate);
}

