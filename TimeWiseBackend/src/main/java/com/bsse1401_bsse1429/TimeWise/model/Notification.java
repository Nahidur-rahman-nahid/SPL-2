package com.bsse1401_bsse1429.TimeWise.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Set;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    private ObjectId notificationId;
    private String sender; // Username of the sender
    private Set<String> recipients; // Usernames of the recipients
    private String notificationSubject; // Enum for notification subjects
    private String notificationMessage;
    private String notificationStatus; // Seen, Unseen
    private String entityNameRelatedToNotification; // For team-related notifications
    private Date timeStamp;

}
