package com.bsse1401_bsse1429.TimeWise.repository;

import com.bsse1401_bsse1429.TimeWise.model.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;

public interface MessageRepository extends MongoRepository<Message, ObjectId> {
    Long countBySender(String userName);

    Long countByRecipientsContains(String userName);

    Long countBySenderAndTimeStampAfter(String userName, Date cutoffDate);

    Long countByRecipientsContainsAndTimeStampAfter(String userName, Date cutoffDate);
}

