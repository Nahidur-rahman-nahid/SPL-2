package com.TimeWise.service;

import com.TimeWise.engine.CollaborationEngine;
import com.TimeWise.utils.MessageBody;
import com.TimeWise.utils.NotificationRequestBody;
import com.TimeWise.utils.UserCredentials;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    public String sendMessage(MessageBody messageBody){
        String  currentUser= UserCredentials.getCurrentUsername();

        return CollaborationEngine.sendMessage(
                currentUser,
                messageBody.getRecipient(),
                messageBody.getMessageSubject(),
                messageBody.getMessageDescription());


    }
}
