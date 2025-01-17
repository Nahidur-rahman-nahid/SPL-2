package com.bsse1401_bsse1429.TimeWise.service;


import com.bsse1401_bsse1429.TimeWise.engine.CollaborationEngine;
import com.bsse1401_bsse1429.TimeWise.utils.NotificationRequestBody;
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
