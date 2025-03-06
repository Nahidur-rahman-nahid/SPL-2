package com.TimeWise.service;

import com.TimeWise.engine.CollaborationEngine;
import com.TimeWise.utils.MessageBody;
import com.TimeWise.utils.UserCredentials;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    public ResponseEntity<?> sendMessage(MessageBody messageBody){
        String  currentUser= UserCredentials.getCurrentUsername();

        return CollaborationEngine.sendMessage(
                currentUser,
                messageBody.getRecipient(),
                messageBody.getMessageSubject(),
                messageBody.getMessageDescription());


    }
}
