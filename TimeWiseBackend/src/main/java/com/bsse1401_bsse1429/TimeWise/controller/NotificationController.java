package com.bsse1401_bsse1429.TimeWise.controller;

import com.bsse1401_bsse1429.TimeWise.service.NotificationService;
import com.bsse1401_bsse1429.TimeWise.service.TaskService;
import com.bsse1401_bsse1429.TimeWise.utils.NotificationRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send/message/user")
    public String sendMessageToUser(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
        return notificationService.sendNotification(notificationBody);
    }
    @PostMapping("/send/message/team")
    public String sendMessageToTeam(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
        return notificationService.sendNotification(notificationBody);
    }
    @PostMapping("/send/message/timewise")
    public String sendMessageToTimeWise(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
        return notificationService.sendNotification(notificationBody);
    }
    @PostMapping("/send/invitation/team")
    public String sendInvitationToJoinTeam(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
        return notificationService.sendNotification(notificationBody);
    }
    @PostMapping("/send/invitation/task")
    public String sendInvitationToJoinTask(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
        return notificationService.sendNotification(notificationBody);
    }
    @PostMapping("/response/invitation/team")
    public String responseToInvitationToJoinTeam(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
        return notificationService.sendNotification(notificationBody);
    }
    @PostMapping("/response/invitation/task")
    public String  responseToInvitationToJoinTask(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
        return notificationService.sendNotification(notificationBody);
    }
    @PostMapping("/send/request/team")
    public String sendRequestToJoinTeam(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
        return notificationService.sendNotification(notificationBody);
    }
    @PostMapping("/send/request/task")
    public String sendRequestToJoinTask(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
        return notificationService.sendNotification(notificationBody);
    }
    @PostMapping("/response/request/team")
    public String responseToRequestToJoinTeam(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
        return notificationService.sendNotification(notificationBody);
    }
    @PostMapping("/response/request/task")
    public String  responseToRequestToJoinTask(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
        return notificationService.sendNotification(notificationBody);
    }
}
