package com.TimeWise.service;


import com.TimeWise.engine.CollaborationEngine;
import com.TimeWise.utils.NotificationRequestBody;
import com.TimeWise.utils.UserCredentials;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

//    public ResponseEntity<?> sendNotification(NotificationRequestBody.SendNotification notificationBody) {
//        String notificationSubject = notificationBody.getNotificationSubject();
//        String entityName = notificationBody.getEntityName();
//        String sender = UserCredentials.getCurrentUsername();
//        String recipient = notificationBody.getRecipient();
//        String message = notificationBody.getMessageContent();
//        switch (notificationSubject) {
//            case "TEAM_INVITATION":
//            case "TASK_INVITATION":
//                return CollaborationEngine.handleInvitation(entityName, sender, recipient, notificationSubject);
//            case "TEAM_INVITATION_RESPONSE":
//            case "TASK_INVITATION_RESPONSE":
//                return CollaborationEngine.handleInvitationResponse(entityName, sender, recipient, notificationSubject, message);
//
//        }
//        return ResponseEntity.ok("Notification sent successfully.");
//    }

}
