package com.bsse1401_bsse1429.TimeWise.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
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



    // Enum for centralized notification subjects
    public enum NotificationSubject {
        TEAM_INVITATION,
        TASK_INVITATION,
        REPLY_TO_TEAM_INVITATION,
        REPLY_TO_TASK_INVITATION,
        ASK_TO_JOIN_TEAM,
        REPLY_TO_JOIN_TEAM_REQUEST,
        ASK_TO_JOIN_TASK,
        REPLY_TO_JOIN_TASK_REQUEST,
        TEAM_MESSAGE,
        USER_MESSAGE,
        SYSTEM_MESSAGE
    }

    // Centralized method for creating notifications
//    public static Notification createNotification(
//            String sender,
//            List<String> recipients,
//            NotificationSubject subject,
//            String message,
//            String teamName,
//            ObjectId taskId) {
//        Notification notification = new Notification();
//        notification.setSender(sender);
//        notification.setRecipients(recipients);
//        notification.setNotificationSubject(subject);
//        notification.setNotificationMessage(message);
//        notification.setNotificationStatus("Unseen");
//        notification.setTeamName(teamName);
//        notification.setTaskId(taskId);
//        notification.setTimestamp(new Date());
//        return notification;
//    }
}
