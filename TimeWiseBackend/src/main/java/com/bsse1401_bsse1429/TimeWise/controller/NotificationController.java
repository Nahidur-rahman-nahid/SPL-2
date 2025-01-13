package com.bsse1401_bsse1429.TimeWise.controller;

import com.bsse1401_bsse1429.TimeWise.engine.CollaborationEngine;
import com.bsse1401_bsse1429.TimeWise.utils.NotificationRequestBody;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {


    @PostMapping("/send/message/user")
    public String sendMessageToUser(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
        return sendNotification(notificationBody);
    }
    @PostMapping("/send/message/team")
    public String sendMessageToTeam(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
        return sendNotification(notificationBody);
    }
    @PostMapping("/send/message/timewise")
    public String sendMessageToTimeWise(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
        return sendNotification(notificationBody);
    }
    @PostMapping("/send/invitation/team")
    public String sendInvitationToJoinTeam(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
        return sendNotification(notificationBody);
    }
    @PostMapping("/send/invitation/task")
    public String sendInvitationToJoinTask(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
        return sendNotification(notificationBody);
    }
    @PostMapping("/response/invitation/team")
    public String responseToInvitationToJoinTeam(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
        return sendNotification(notificationBody);
    }
    @PostMapping("/response/invitation/task")
    public String  responseToInvitationToJoinTask(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
        return sendNotification(notificationBody);
    }
    @PostMapping("/send/request/team")
    public String sendRequestToJoinTeam(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
        return sendNotification(notificationBody);
    }
    @PostMapping("/response/request/team")
    public String responseToRequestToJoinTeam(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
        return sendNotification(notificationBody);
    }

    public String sendNotification(NotificationRequestBody.SendNotification notificationBody){
        return CollaborationEngine.sendNotification(
                notificationBody.getEntityName(),
                notificationBody.getNotificationSubject(),
                notificationBody.getRecipient(),
                notificationBody.getMessageContent());

    }

}
