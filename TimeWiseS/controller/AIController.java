package com.TimeWise.controller;

import com.TimeWise.model.Task;
import com.TimeWise.service.AIService;
import com.TimeWise.utils.GenerateTaskRequestBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIService aiService;

    @GetMapping("/generate/tasks")
    public ResponseEntity<Map<String, Object>> generateTasks(@RequestBody GenerateTaskRequestBody requestBody) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = dateFormat.format(new java.util.Date());

            // Extract the deadline from the request body if provided
            String userDeadline = requestBody.getGoalDeadline() != null ? dateFormat.format(requestBody.getGoalDeadline()) : "";

            // Modify prompt to allow AI to suggest deadlines if not provided
            String prompt = String.format(
                    "You are an AI assistant helping with goal planning. The goal is '%s'. "
                            + "Create a structured list of tasks to achieve this goal. "
                            + "If no specific deadline is provided by the user, you should: "
                            + "1. Estimate a reasonable timeline for completing the goal "
                            + "2. Distribute tasks across this timeline "
                            + "3. Ensure the overall goal is achievable "
                            + "Each task must include: "
                            + "taskName, taskDeadline, and taskTodos. "
                            + "Start planning from the current date '%s'. "
                            + "If a deadline is provided by the user ('%s'), ensure the tasks are scheduled within this timeframe. "
                            + "Each task **must** have at least one taskTodo, which contains "
                            + "a description. But more than one is better in certain cases add so if such cases exist. "
                            + "Respond in **valid JSON** format as a list of task objects. "
                            + "Ensure the response contains a reasonable number of tasks (between 3 to 10 tasks).",
                    requestBody.getGoalName(), currentDate, userDeadline
            );

            // Configure ObjectMapper to handle Date parsing
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            // Register a custom date format for parsing
            SimpleDateFormat taskDeadlineFormat = new SimpleDateFormat("yyyy-MM-dd");
            objectMapper.setDateFormat(taskDeadlineFormat);

            // Call AI service to generate response
            String aiResponse = aiService.generateResponse(prompt);

            System.out.println("AI Response: " + aiResponse);

            // Extract the valid JSON portion from the AI response
            String jsonResponse = extractJsonFromResponse(aiResponse);

            // Parse AI response into a list of Task objects
            List<Task> tasks = objectMapper.readValue(jsonResponse, new TypeReference<List<Task>>() {});
            String taskGoal = requestBody.getGoalName();
            for (Task task : tasks) {
                task.setTaskGoal(taskGoal);
            }

            // Ensure the response contains a reasonable number of tasks
            if (tasks.size() < 3 || tasks.size() > 10) {
                throw new Exception("Generated tasks count is not within the expected range (3 to 10 tasks).");
            }

            // Format the response for display
            Map<String, Object> response = new HashMap<>();
            response.put("goal", requestBody.getGoalName());
            response.put("deadline", userDeadline.isEmpty() ? "Not specified" : userDeadline);
            response.put("tasks", tasks);

            return ResponseEntity.ok(response);

        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to parse AI response into tasks",
                            "details", e.getMessage(),
                            "aiResponse", e.getOriginalMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred",
                            "details", e.getMessage()));
        }
    }

    // Helper method to extract JSON from the AI response
    private String extractJsonFromResponse(String aiResponse) {
        // Look for the start and end of the JSON block
        int jsonStart = aiResponse.indexOf("```json");
        int jsonEnd = aiResponse.lastIndexOf("```");

        if (jsonStart == -1 || jsonEnd == -1) {
            throw new RuntimeException("Invalid AI response format: JSON block not found.");
        }

        // Extract the JSON portion
        String jsonResponse = aiResponse.substring(jsonStart + 7, jsonEnd).trim();

        return jsonResponse;
    }

    @GetMapping("/autocomplete/message")
    public String generateMessage(@RequestParam String messageSubject) {
        String totalPrompt = "Generate a concise, professional message body for an email with the subject: '"
                + messageSubject + "'. "
                + "Provide only the raw message text that can be directly sent. "
                + "Avoid any metadata, greetings, or additional context. "
                + "Keep it brief, clear, and to the point.";

        String aiResponse = aiService.generateResponse(totalPrompt);
        return extractTextContent(aiResponse);
    }

    @GetMapping("/autocomplete/feedback")
    public String generateFeedback(@RequestParam String feedbackSubject) {
        String totalPrompt = "Generate a constructive, professional feedback message for the subject: '"
                + feedbackSubject + "'. "
                + "Provide only the raw feedback text that can be directly sent. "
                + "Focus on specific, actionable insights. "
                + "Avoid formal greetings or metadata. "
                + "Be direct, respectful, and solution-oriented.";

        String aiResponse = aiService.generateResponse(totalPrompt);
        return extractTextContent(aiResponse);
    }
    private String extractTextContent(String aiResponse) {
        // Remove any leading/trailing whitespace and quotes
        aiResponse = aiResponse.trim().replaceAll("^\"|\"$", "");

        // Try different extraction methods
        String[] extractionPatterns = {
                "textContent=",
                "message=",
                "body=",
                "suggestion="
        };

        for (String pattern : extractionPatterns) {
            int startIndex = aiResponse.indexOf(pattern);
            if (startIndex != -1) {
                String extractedText = aiResponse.substring(startIndex + pattern.length());

                // Remove metadata and trim
                extractedText = extractedText.split(",\\s*metadata=")[0]
                        .split(",\\s*")[0]
                        .trim()
                        .replaceAll("^['\"]|['\"]$", "");

                return extractedText;
            }
        }

        // If no specific pattern found, return the entire response
        return aiResponse;
    }


}
