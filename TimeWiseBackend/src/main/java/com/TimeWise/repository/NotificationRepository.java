package com.TimeWise.repository;

import com.TimeWise.model.Notification;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, ObjectId> {

    List<Notification> findByRecipientsContains(String currentUserName);
}
