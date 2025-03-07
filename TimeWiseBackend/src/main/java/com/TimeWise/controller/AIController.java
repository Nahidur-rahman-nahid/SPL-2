package com.TimeWise.controller;

import com.TimeWise.model.Task;
import com.TimeWise.service.AIService;
import com.TimeWise.utils.GenerateTaskRequestBody;
import com.TimeWise.utils.GeneratedTask;
import com.TimeWise.utils.UserDetailResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIService aiService;

    @PostMapping("/roadmap/generate")
    public ResponseEntity<Map<String, Object>> generateRoadmap(@RequestBody GenerateTaskRequestBody requestBody) {
       return aiService.generateRoadmap(requestBody);
    }

    @GetMapping("/message/autocomplete")
    public String generateMessage(@RequestParam String messageSubject) {
        return aiService.generateMessage(messageSubject);
    }
    @GetMapping("/message/summarize")
    public String summarizeMessages() {
        return aiService.summarizeMessages();
    }

    @GetMapping("/feedback/autocomplete")
    public String generateFeedback(@RequestParam String feedbackSubject) {
        return aiService.generateFeedback(feedbackSubject);
    }

    @GetMapping("/feedback/summarize")
    public String summarizeFeedbacks() {
        return aiService.summarizeFeedbacks();
    }
    @PostMapping("/session/generate")
    public ResponseEntity<?> generateSession(@RequestBody List<UserDetailResponse.UserTasks> userTasks,@RequestParam(required = false) String userPrompt) {
        return aiService.generateSession(userTasks,userPrompt);
    }

    @PostMapping("/analyze")
    public ResponseEntity<String> analyzeData(
            @RequestBody Object data,
            @RequestParam(required = false) String context,
            @RequestParam(required = false, defaultValue = "standard") String analysisType) {

        return ResponseEntity.ok(aiService.generateGenericInsight(data, context, analysisType));
    }

    @PostMapping("/analyze-list")
    public ResponseEntity<String> analyzeDataList(
            @RequestBody List<Object> dataList,
            @RequestParam(required = false) String context,
            @RequestParam(required = false, defaultValue = "standard") String analysisType) {

        return ResponseEntity.ok(aiService.generateGenericInsight(dataList, context, analysisType));
    }

//    @PostMapping("/analyze-map")
//    public ResponseEntity<String> analyzeDataMap(
//            @RequestBody Map<String, Object> dataMap,
//            @RequestParam(required = false) String context,
//            @RequestParam(required = false, defaultValue = "standard") String analysisType) {
//
//        return ResponseEntity.ok(aiService.generateGenericInsight(dataMap, context, analysisType));
//    }


}
