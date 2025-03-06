package com.TimeWise.service;


import com.TimeWise.engine.CollaborationEngine;
import com.TimeWise.utils.NotificationRequestBody;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public static String sendNotification(NotificationRequestBody.SendNotification notificationBody){

        return CollaborationEngine.sendNotification(
                notificationBody.getEntityName(),
                notificationBody.getNotificationSubject(),
                notificationBody.getRecipient(),
                notificationBody.getMessageContent());

    }


}
