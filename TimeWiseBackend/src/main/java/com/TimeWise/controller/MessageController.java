package com.TimeWise.controller;


import com.TimeWise.engine.CollaborationEngine;
import com.TimeWise.service.MessageService;
import com.TimeWise.service.TaskService;
import com.TimeWise.utils.MessageBody;
import com.TimeWise.utils.NotificationRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;


    @PostMapping("/send/user")
    public String sendMessageToUser(@RequestBody MessageBody messageBody) {
        return messageService.sendMessage(messageBody);
    }
    @PostMapping("/send/team")
    public String sendMessageToTeam(@RequestBody MessageBody messageBody) {
        return messageService.sendMessage(messageBody);
    }
    @PostMapping("/send/timewise")
    public String sendMessageToTimeWise(@RequestBody MessageBody messageBody) {
        return messageService.sendMessage(messageBody);
    }
}
