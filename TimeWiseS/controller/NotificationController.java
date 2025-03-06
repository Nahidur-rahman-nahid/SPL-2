package com.TimeWise.controller;

import com.TimeWise.service.NotificationService;
import com.TimeWise.utils.NotificationRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

//    @Autowired
//    private NotificationService notificationService;
//
//    @PostMapping("/send/invitation/team")
//    public ResponseEntity<?> sendInvitationToJoinTeam(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
//        return notificationService.sendNotification(notificationBody);
//    }
//    @PostMapping("/send/invitation/task")
//    public ResponseEntity<?> sendInvitationToJoinTask(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
//        return notificationService.sendNotification(notificationBody);
//    }
//    @PostMapping("/response/invitation/team")
//    public ResponseEntity<?> responseToInvitationToJoinTeam(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
//        return notificationService.sendNotification(notificationBody);
//    }
//    @PostMapping("/response/invitation/task")
//    public ResponseEntity<?>  responseToInvitationToJoinTask(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
//        return notificationService.sendNotification(notificationBody);
//    }
//    @PostMapping("/send/request/team")
//    public ResponseEntity<?> sendRequestToJoinTeam(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
//        return notificationService.sendNotification(notificationBody);
//    }
//    @PostMapping("/response/request/team")
//    public ResponseEntity<?> responseToRequestToJoinTeam(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
//        return notificationService.sendNotification(notificationBody);
//    }



}
