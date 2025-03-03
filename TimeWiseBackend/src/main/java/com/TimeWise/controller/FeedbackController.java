package com.TimeWise.controller;


import com.TimeWise.service.FeedbackService;
import com.TimeWise.utils.FeedbackBody;
import com.TimeWise.utils.MessageBody;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        return feedbackService.getAllFeedbacks();
    }
    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody FeedbackBody feedbackBody) {
        return feedbackService.sendFeedback(feedbackBody);
    }
    @DeleteMapping("/remove")
    public ResponseEntity<?> remove(@RequestParam ObjectId feedbackId) {
        return feedbackService.removeFeedback(feedbackId);
    }
}
