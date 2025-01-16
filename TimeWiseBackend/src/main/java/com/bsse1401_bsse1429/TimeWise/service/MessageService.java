package com.bsse1401_bsse1429.TimeWise.service;

import com.bsse1401_bsse1429.TimeWise.engine.CollaborationEngine;
import com.bsse1401_bsse1429.TimeWise.utils.MessageBody;
import com.bsse1401_bsse1429.TimeWise.utils.NotificationRequestBody;
import com.bsse1401_bsse1429.TimeWise.utils.UserCredentials;
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
