package com.TimeWise.service;

import com.TimeWise.engine.CollaborationEngine;
import com.TimeWise.model.Feedback;
import com.TimeWise.model.Message;
import com.TimeWise.repository.FeedbackRepository;
import com.TimeWise.repository.MessageRepository;
import com.TimeWise.utils.FeedbackBody;
import com.TimeWise.utils.MessageBody;
import com.TimeWise.utils.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;


    public ResponseEntity<?> sendMessage(MessageBody messageBody){
        String  currentUser= UserCredentials.getCurrentUsername();

        return CollaborationEngine.sendMessage(
                currentUser,
                messageBody.getRecipient(),
                messageBody.getMessageSubject(),
                messageBody.getMessageDescription());


    }
    public ResponseEntity<?> getAllMessages() {
        String currentUser = UserCredentials.getCurrentUsername();
        List<Message> userMessages = messageRepository.findBySenderOrRecipientsContains(currentUser);

        if (userMessages == null || userMessages.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return ResponseEntity.ok(userMessages);
    }

}
