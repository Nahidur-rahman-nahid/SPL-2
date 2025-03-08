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
        List<Feedback> userFeedbacks=feedbackRepository.findByFeedbackSenderOrFeedbackRecipient(currentUser,currentUser);
        if (userFeedbacks == null || userFeedbacks.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(userFeedbacks);
    }


    public ResponseEntity<?> removeFeedback(Feedback feedback) {
        String currentUser = UserCredentials.getCurrentUsername();
        ObjectId feedbackId = feedback.getFeedbackId(); // Get feedbackId from the received object.

        if (feedbackId == null) {
            return ResponseEntity.badRequest().body("Feedback ID is missing.");
        }

        Feedback existingFeedback = feedbackRepository.findByFeedbackId(feedbackId);

        if (existingFeedback == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid feedback ID.");
        }

        if (existingFeedback.getFeedbackSender() != null && existingFeedback.getFeedbackSender().equals(currentUser)) {
            existingFeedback.setFeedbackSender(null);
        } else if (existingFeedback.getFeedbackRecipient() != null && existingFeedback.getFeedbackRecipient().equals(currentUser)) {
            existingFeedback.setFeedbackRecipient(null);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not the sender or recipient of this feedback.");
        }

        if (existingFeedback.getFeedbackRecipient() == null && existingFeedback.getFeedbackSender() == null) {
            feedbackRepository.delete(existingFeedback);
            return ResponseEntity.ok("Feedback deleted successfully.");
        } else {
            feedbackRepository.save(existingFeedback);
            return ResponseEntity.ok("Feedback sender/recipient removed successfully.");
        }
    }
}
