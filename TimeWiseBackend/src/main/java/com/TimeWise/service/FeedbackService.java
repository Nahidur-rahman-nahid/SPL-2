package com.TimeWise.service;

import com.TimeWise.engine.CollaborationEngine;
import com.TimeWise.model.Feedback;
import com.TimeWise.repository.FeedbackRepository;
import com.TimeWise.utils.FeedbackBody;
import com.TimeWise.utils.UserCredentials;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    public ResponseEntity<?> sendFeedback(FeedbackBody feedbackBody) {
        String  currentUser= UserCredentials.getCurrentUsername();
        return CollaborationEngine.sendFeedback(
                currentUser,
                feedbackBody.getFeedbackRecipient(),
                feedbackBody.getFeedbackMessage(),
                feedbackBody.getFeedbackTaskName(),
                feedbackBody.getFeedbackScore()
        );
    }
    public ResponseEntity<?> getAllFeedbacks() {
        String  currentUser= UserCredentials.getCurrentUsername();
        List<Feedback> userFeedbacks=feedbackRepository.findByFeedbackSenderOrFeedbackRecipient(currentUser);
        if (userFeedbacks == null || userFeedbacks.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(userFeedbacks);
    }


    public ResponseEntity<?> removeFeedback(ObjectId feedbackId) {
        String currentUser = UserCredentials.getCurrentUsername();
        Feedback feedback = feedbackRepository.findByFeedbackId(feedbackId);

        if (feedback == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid feedback ID.");
        }

        if (feedback.getFeedbackSender() != null && feedback.getFeedbackSender().equals(currentUser)) {
            feedback.setFeedbackSender(null);
        } else if (feedback.getFeedbackRecipient() != null && feedback.getFeedbackRecipient().equals(currentUser)) {
            feedback.setFeedbackRecipient(null);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not the sender or recipient of this feedback.");
        }

        if (feedback.getFeedbackRecipient() == null && feedback.getFeedbackSender() == null) {
            feedbackRepository.delete(feedback);
            return ResponseEntity.ok("Feedback deleted successfully.");
        } else {
            feedbackRepository.save(feedback);
            return ResponseEntity.ok("Feedback sender/recipient removed successfully.");
        }
    }

}
