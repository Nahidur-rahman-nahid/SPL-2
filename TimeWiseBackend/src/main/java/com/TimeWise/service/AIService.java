package com.TimeWise.service;

import com.TimeWise.model.Feedback;
import com.TimeWise.model.Message;
import com.TimeWise.repository.FeedbackRepository;
import com.TimeWise.repository.MessageRepository;
import com.TimeWise.repository.TaskRepository;
import com.TimeWise.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.MistralAiChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AIService {

    private final MistralAiChatModel chatModel;

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;


    public AIService(
            @Value("${spring.ai.mistralai.api-key}") String apiKey,
            @Value("${spring.ai.mistralai.chat.options.model}") String model,
            @Value("${spring.ai.mistralai.chat.options.temperature}") double temperature) {
        var mistralAiApi = new MistralAiApi(apiKey);
        this.chatModel = new MistralAiChatModel(
                mistralAiApi,
                MistralAiChatOptions.builder()
                        .withModel(model)
                        .withTemperature(temperature)
                        .withMaxTokens(1000) // Increased max tokens for detailed task generation
                        .build()
        );
    }

    public String generateResponse(String prompt) {
        // Use the Mistral AI model to generate the task list
        var results = chatModel.call(new Prompt(prompt)).getResults();

        // Combine results into a single JSON string
        // Handle nulls
        // Ensure the getOutput() is an Optional<AssistantMessage> and handle it properly
        // Ensure proper handling of AssistantMessage
        // Convert AssistantMessage to String if necessary

        return results.stream()
                .map(g -> Optional.ofNullable(g.getOutput())
                        .orElse(new AssistantMessage("", Collections.emptyMap())))  // Use empty map instead of null
                .map(AssistantMessage::toString)  // Convert AssistantMessage to String if necessary
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    public ResponseEntity<Map<String, Object>> generateRoadmap(GenerateTaskRequestBody requestBody) {
        try {


            String prompt = "You are an expert AI assistant specializing in goal planning and task breakdown. "
                    + "Your primary objective is to generate a comprehensive and actionable roadmap to achieve the user's goal. ";

            if (requestBody.getGoalName() != null && !requestBody.getGoalName().isEmpty()) {
                prompt += "The user's goal is: '" + requestBody.getGoalName() + "'. ";
            } else {
                prompt += "The user has not provided a goal. You must ask the user to provide a goal. ";
                // Consider returning an error or early exit here if goal is required.
                // return "Error: Goal name is required.";
            }

            String userDeadline = (requestBody.getGoalDeadline() != null) ? requestBody.getGoalDeadline().toString() : "";
            String currentDate = java.time.LocalDate.now().toString();

            if (requestBody.getGoalDeadline() != null) {
                prompt += "The user has provided a deadline: '" + userDeadline + "'. All tasks must be scheduled within this timeframe. ";
            } else {
                prompt += "The user has not provided a deadline. You must analyze the goal and estimate a realistic timeline for its completion. "
                        + "Distribute the tasks across this estimated timeline in a logical sequence. "
                        + "Ensure the overall plan is achievable and the timeline is reasonable. ";
            }

            if (requestBody.getNumberOfTask() != null) {
                int numberOfTasks = requestBody.getNumberOfTask();
                if (numberOfTasks >= 1 && numberOfTasks <= 7) {
                    prompt += "The user has specified the number of tasks: " + numberOfTasks + ". ";
                    prompt += "Adjust the granularity of each task and the number of sub-tasks (taskTodos) accordingly. ";

                    if (numberOfTasks == 1) {
                        prompt += "Since only one task is requested, it may have a larger number of sub-tasks (taskTodos), possibly up to 10, to cover all necessary steps. ";
                    } else {
                        prompt += "For " + numberOfTasks + " tasks, each task should typically contain 3 to 6 sub-tasks (taskTodos). ";
                    }

                } else {
                    prompt += "The number of tasks specified by the user (" + numberOfTasks + ") is invalid. It must be between 1 and 7. "
                            + "Please generate a roadmap with a default number of tasks, ideally between 3 and 6, while maintaining high quality and effectiveness. ";
                }
            } else {
                prompt += "The user has not specified the number of tasks. Please generate a roadmap with a reasonable number of tasks, ideally between 3 and 6, to ensure a balanced and effective plan. ";
                prompt += "Dynamically determine the number of sub-tasks (taskTodos) for each task based on the task's complexity, ensuring each task has at least 3 sub-tasks.";
            }

            if (requestBody.getUserPrompt() != null && !requestBody.getUserPrompt().isEmpty()) {
                prompt += "The user has provided additional considerations to personalize the roadmap: \"" + requestBody.getUserPrompt() + "\". "
                        + "Please integrate these considerations into your task creation process to make the roadmap more tailored. "
                        + "If any part of the prompt is unclear or irrelevant, focus on the core goal and your own expertise. ";
            }

            prompt += "Start planning from the current date: '" + currentDate + "'. ";
            prompt += "Each task in your roadmap **must** include the following fields: "
                    + "   - 'taskName': A concise and descriptive name for the task. "
                    + "   - 'taskDeadline': The deadline for completing the task, in 'yyyy-MM-dd'T'HH:mm:ss.SSSX' format. "
                    + "   - 'taskTodos': A list of strings, each representing an actionable step. "
                    + "Each task **must** have at least 3 entries in the 'taskTodos' array with detailed descriptions. "
                    + "Tasks should be arranged in a chronological order, forming a logical roadmap. "
                    + "Respond with a **valid JSON array** of task objects.";
            // Call AI service to generate response
            String aiResponse = generateResponse(prompt);


            // Extract the valid JSON portion from the AI response
            String jsonResponse = extractJsonFromResponse(aiResponse);

            // Parse the raw JSON to a JsonNode first to handle the structure conversion
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            // Register date handling module
            objectMapper.registerModule(new JavaTimeModule());
            SimpleDateFormat taskDeadlineFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            objectMapper.setDateFormat(taskDeadlineFormat);

            // First parse the JSON to a JsonNode to have more control
            JsonNode tasksNode = objectMapper.readTree(jsonResponse);
            List<GeneratedTask> generatedTasks = new ArrayList<>();

            // Manually convert each task object to handle the taskTodos differently
            for (JsonNode taskNode : tasksNode) {
                GeneratedTask generatedTask = new GeneratedTask();

                // Set basic task properties
                generatedTask.setTaskName(taskNode.get("taskName").asText());
                // Parse the deadline string to a Date object
                try {
                    String deadlineStr = taskNode.get("taskDeadline").asText();
                    generatedTask.setTaskDeadline(taskDeadlineFormat.parse(deadlineStr));
                } catch (ParseException e) {
                    throw new JsonProcessingException("Invalid date format") {
                    };
                }

                // Set default task properties
                generatedTask.setTaskGoal(requestBody.getGoalName());
                generatedTask.setTaskCategory("General");
                generatedTask.setTaskPriority("Medium");
                generatedTask.setTaskDescription("A task to achieve the goal " + requestBody.getGoalName());
                generatedTask.setTaskVisibilityStatus("Public");

                generatedTask.setTaskTodos(new ArrayList<>());
                JsonNode todosNode = taskNode.get("taskTodos");
                if (todosNode != null && todosNode.isArray()) {
                    for (JsonNode todoNode : todosNode) {

                        generatedTask.getTaskTodos().add(todoNode.asText());

                    }
                }

                generatedTasks.add(generatedTask);
            }


            // Format the response for display
            Map<String, Object> response = new HashMap<>();
            response.put("goal", requestBody.getGoalName());
            response.put("deadline", userDeadline.isEmpty() ? "Not specified" : userDeadline);
            response.put("tasks", generatedTasks);

            return ResponseEntity.ok(response);

        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to parse AI response into tasks",
                            "details", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred",
                            "details", e.getMessage()));
        }
    }

    /**
     * Helper method to extract JSON from the AI response
     * This improved version handles various formats the AI might return
     */
//    private String extractJsonFromResponse(String aiResponse) {
//        // Check if the response is already a valid JSON array
//        if (aiResponse.trim().startsWith("[") && aiResponse.trim().endsWith("]")) {
//            return aiResponse.trim();
//        }
//
//        // Look for JSON code block
//        int jsonStart = aiResponse.indexOf("```json");
//        if (jsonStart != -1) {
//            // Find the end of the code block
//            int jsonEnd = aiResponse.indexOf("```", jsonStart + 7);
//            if (jsonEnd != -1) {
//                // Extract the JSON portion without the markdown code block markers
//                return aiResponse.substring(jsonStart + 7, jsonEnd).trim();
//            }
//        }
//
//        // Look for just a regular code block
//        jsonStart = aiResponse.indexOf("```");
//        if (jsonStart != -1) {
//            int jsonEnd = aiResponse.indexOf("```", jsonStart + 3);
//            if (jsonEnd != -1) {
//                String possibleJson = aiResponse.substring(jsonStart + 3, jsonEnd).trim();
//                // Verify it looks like JSON
//                if (possibleJson.startsWith("[") && possibleJson.endsWith("]")) {
//                    return possibleJson;
//                }
//            }
//        }
//
//        // If we can't find markdown code blocks, try to find the JSON array directly
//        jsonStart = aiResponse.indexOf("[");
//        int jsonEnd = aiResponse.lastIndexOf("]");
//        if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
//            return aiResponse.substring(jsonStart, jsonEnd + 1);
//        }
//
//        // If we still can't find valid JSON, throw an exception
//        throw new RuntimeException("Invalid AI response format: JSON array not found.");
//    }
    public String generateMessage(String messageSubject) {
        String totalPrompt = "Generate an email message body with the following format:\n" +
                "Message Subject: [Subject generated by AI]\n" +
                "Message Description: [Message body generated by AI]\n" +
                "\n" +
                "The subject should be related to the input subject: '" + messageSubject + "'.\n" +
                "The message body should be concise and professional.\n" +
                "Provide only the raw text, without any greetings or additional context.";


        String aiResponse = generateResponse(totalPrompt);
        return extractTextContent(aiResponse);
    }


    public String generateFeedback(String feedbackSubject) {
        String totalPrompt = "Generate a feedback message with the following format:\n" +
                "Feedback Subject: [Subject generated by AI]\n" +
                "Feedback Description: [Feedback body generated by AI]\n" +
                "\n" +
                "The subject should be related to the input subject: '" + feedbackSubject + "'.\n" +
                "The feedback body should be constructive and professional.\n" +
                "Focus on specific, actionable insights.\n" +
                "Avoid formal greetings or metadata.\n" +
                "Be direct, respectful, and solution-oriented.\n" +
                "Provide only the raw text, without any greetings or additional context.";


        String aiResponse = generateResponse(totalPrompt);
        return extractTextContent(aiResponse);
    }
//    private String extractTextContent(String aiResponse) {
//        // Remove any leading/trailing whitespace and quotes
//        aiResponse = aiResponse.trim().replaceAll("^\"|\"$", "");
//
//        // Try different extraction methods
//        String[] extractionPatterns = {
//                "textContent=",
//                "message=",
//                "body=",
//                "suggestion="
//        };
//
//        for (String pattern : extractionPatterns) {
//            int startIndex = aiResponse.indexOf(pattern);
//            if (startIndex != -1) {
//                String extractedText = aiResponse.substring(startIndex + pattern.length());
//
//                // Remove metadata and trim
//                extractedText = extractedText.split(",\\s*metadata=")[0]
//                        .split(",\\s*")[0]
//                        .trim()
//                        .replaceAll("^['\"]|['\"]$", "");
//
//                return extractedText;
//            }
//        }
//
//        // If no specific pattern found, return the entire response
//        return aiResponse;
//    }

    public String summarizeMessages() {
        String currentUser = UserCredentials.getCurrentUsername();
        List<Message> userMessages = messageRepository.findByRecipientsContains(currentUser);

        if (userMessages == null || userMessages.isEmpty()) {
            return "You do not have any messages to summarize";
        }

        // Sort messages by date (newest first) and limit to most recent 50
        userMessages.sort(Comparator.comparing(Message::getTimeStamp).reversed());
        List<Message> recentMessages = userMessages.stream()
                .limit(50)
                .toList();

        // Prepare message data for AI prompt
        StringBuilder messagesContent = new StringBuilder();
        for (int i = 0; i < recentMessages.size(); i++) {
            Message message = recentMessages.get(i);
            messagesContent.append("Message ").append(i + 1).append(":\n");
            messagesContent.append("Subject: ").append(message.getMessageSubject()).append("\n");
            messagesContent.append("Content: ").append(message.getMessageDescription()).append("\n\n");
        }

        // Create AI prompt for summarization
        String prompt = "Analyze and summarize the following messages for user " + currentUser + ":\n\n" +
                messagesContent.toString() + "\n" +
                "Please provide a comprehensive summary with the following sections:\n" +
                "Key Findings: Identify the main themes and important information from these messages.\n" +
                "Action Items: List any action items or requests that require attention.\n" +
                "Priority Messages: Highlight the most urgent or important messages.\n" +
                "Communication Patterns: Note any patterns in communication frequency or topics.\n\n" +
                "Format your response with clear section headers (e.g., 'Key Findings:', 'Action Items:') " +
                "with line breaks between sections. Keep your analysis concise but thorough.Don't make it too long just cover and sum up the important aspect also don't miss any crucial aspects or concerns.";

        // Generate AI response
        String aiResponse = generateResponse(prompt);

        // Return the extracted summary
        return extractTextContent(aiResponse);
    }

    public String summarizeFeedbacks() {
        String currentUser = UserCredentials.getCurrentUsername();
        List<Feedback> userFeedbacks = feedbackRepository.findByFeedbackRecipient(currentUser);

        if (userFeedbacks == null || userFeedbacks.isEmpty()) {
            return "You do not have any feedbacks to summarize";
        }

        // Sort feedbacks by date (newest first) and limit to most recent 20
        userFeedbacks.sort(Comparator.comparing(Feedback::getTimeStamp).reversed());
        List<Feedback> recentFeedbacks = userFeedbacks.stream()
                .limit(20)
                .toList();

        // Prepare feedback data for AI prompt
        StringBuilder feedbackContent = new StringBuilder();
        for (int i = 0; i < recentFeedbacks.size(); i++) {
            Feedback feedback = recentFeedbacks.get(i);
            feedbackContent.append("Feedback ").append(i + 1).append(":\n");
            feedbackContent.append("Task: ").append(feedback.getFeedbackTaskName()).append("\n");
            feedbackContent.append("Score: ").append(feedback.getFeedbackScore()).append("\n");
            feedbackContent.append("Message: ").append(feedback.getFeedbackMessage()).append("\n\n");
        }

        // Create AI prompt for summarization
        String prompt = "Analyze and summarize the following feedback items for user " + currentUser + ":\n\n" +
                feedbackContent.toString() + "\n" +
                "Please provide a comprehensive summary with the following sections:\n" +
                "Overall Performance: Summarize the general performance based on feedback scores and comments.\n" +
                "Strengths: Identify areas where the user is performing well.\n" +
                "Areas for Improvement: Suggest specific areas that need attention.\n" +
                "Actionable Recommendations: Provide practical steps for improvement.\n" +
                "Trends: Note any trends in performance over time.\n\n" +
                "Format your response with clear section headers (e.g., 'Overall Performance:', 'Strengths:') " +
                "with line breaks between sections. Keep your analysis concise but thorough.Don't make it too long just cover and sum up the important aspect also don't miss any crucial aspects or concerns.";

        // Generate AI response
        String aiResponse = generateResponse(prompt);

        // Return the extracted summary
        return extractTextContent(aiResponse);
    }

    /**
     * Improved method to extract text content from AI response
     * This handles various formatting possibilities and preserves section structure
     */
    private String extractTextContent(String aiResponse) {
        // Remove any markdown code block markers if present
        aiResponse = aiResponse.replaceAll("```[a-zA-Z]*\\n", "").replaceAll("```", "");

        // Remove any AssistantMessage formatting if present
        if (aiResponse.contains("textContent=")) {
            aiResponse = aiResponse.split("textContent=")[1]
                    .split(",\\s*metadata=")[0]
                    .trim()
                    .replaceAll("^['\"]|['\"]$", "");
        }

        // Clean up any extra quotation marks or escape characters
        aiResponse = aiResponse.trim()
                .replaceAll("^\"|\"$", "")
                .replace("\\n", "\n")
                .replace("\\\"", "\"");

        // Ensure section headers are properly formatted with line breaks
        String[] knownSections = {
                "Key Findings:", "Action Items:", "Priority Messages:", "Communication Patterns:",
                "Overall Performance:", "Strengths:", "Areas for Improvement:",
                "Actionable Recommendations:", "Trends:"
        };

        for (String section : knownSections) {
            // Ensure there's a line break before each section header (except the first one)
            aiResponse = aiResponse.replace(section, "\n" + section);
        }

        // Remove any double line breaks that might have been created
        while (aiResponse.contains("\n\n\n")) {
            aiResponse = aiResponse.replace("\n\n\n", "\n\n");
        }

        // Ensure the response starts clean (no leading line breaks)
        return aiResponse.replaceAll("^\\n+", "");
    }

    public ResponseEntity<?> generateSession(List<UserDetailResponse.UserTasks> userTasks, String userPrompt) {
        try {
            if (userTasks == null || userTasks.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "sessionGoal", "No tasks available to create a session plan",
                        "tasksOperated", Collections.emptyList(),
                        "sessionOutcome", "Please add tasks to generate a meaningful session plan",
                        "duration", 0 // Default duration
                ));
            }

            // Sort and limit tasks based on priority, deadline, and progress
            List<UserDetailResponse.UserTasks> prioritizedTasks = prioritizeTasks(userTasks);

            // Generate AI prompt
            String prompt = createSessionPrompt(prioritizedTasks, userPrompt);

            // Get AI response
            String aiResponse = generateResponse(prompt);

            // Extract and parse the JSON response
            Map<String, Object> sessionData = extractSessionData(aiResponse);

            return ResponseEntity.ok(sessionData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to generate session plan",
                            "details", e.getMessage()));
        }
    }

    /**
     * Prioritize tasks based on priority, deadline proximity, and low progress
     */
    private List<UserDetailResponse.UserTasks> prioritizeTasks(List<UserDetailResponse.UserTasks> userTasks) {
        // Create a copy of the list to avoid modifying the original
        List<UserDetailResponse.UserTasks> tasksCopy = new ArrayList<>(userTasks);

        // Custom comparator for task prioritization
        Comparator<UserDetailResponse.UserTasks> taskComparator = (t1, t2) -> {
            // First prioritize by task priority
            int priorityCompare = getPriorityWeight(t1.getTaskPriority()) - getPriorityWeight(t2.getTaskPriority());
            if (priorityCompare != 0) return priorityCompare;

            // Then by deadline (closer deadlines first)
            Date now = new Date();
            Date deadline1 = t1.getTaskDeadline() != null ? t1.getTaskDeadline() : new Date(Long.MAX_VALUE);
            Date deadline2 = t2.getTaskDeadline() != null ? t2.getTaskDeadline() : new Date(Long.MAX_VALUE);

            long timeToDeadline1 = deadline1.getTime() - now.getTime();
            long timeToDeadline2 = deadline2.getTime() - now.getTime();

            int deadlineCompare = Long.compare(timeToDeadline1, timeToDeadline2);
            if (deadlineCompare != 0) return deadlineCompare;

            // Then by progress (lower progress first)
            int progress1 = t1.getTaskCurrentProgress() != null ? t1.getTaskCurrentProgress() : 0;
            int progress2 = t2.getTaskCurrentProgress() != null ? t2.getTaskCurrentProgress() : 0;

            return Integer.compare(progress1, progress2);
        };

        // Sort tasks based on the comparator
        tasksCopy.sort(taskComparator);

        // Limit to top 30 tasks
        return tasksCopy.stream().limit(20).collect(Collectors.toList());
    }

    /**
     * Helper method to convert priority string to numeric weight
     */
    private int getPriorityWeight(String priority) {
        if (priority == null) return 3; // Default medium priority

        return switch (priority.toLowerCase()) {
            case "high" -> 1;
            case "medium" -> 2;
            case "low" -> 3;
            default -> 2;
        };
    }

    /**
     * Create comprehensive prompt for AI to generate session plan
     */
    private String createSessionPrompt(List<UserDetailResponse.UserTasks> tasks, String userPrompt) {
        StringBuilder prompt = new StringBuilder();

        // Base instruction
        prompt.append("You are an expert productivity assistant specializing in session planning. ");
        prompt.append("Your task is to analyze the following list of tasks and create a focused, productive session plan. ");
        prompt.append("A good session has a clear, specific goal that can be achieved in a single work period, ");
        prompt.append("and includes a selection of related tasks that can be completed together efficiently.\n\n");

        // Task details
        prompt.append("Here are the user's current tasks, already sorted by priority, deadline, and progress status:\n\n");

        for (int i = 0; i < tasks.size(); i++) {
            UserDetailResponse.UserTasks task = tasks.get(i);
            prompt.append("Task ").append(i + 1).append(":\n");
            prompt.append("- Name: ").append(task.getTaskName()).append("\n");
            prompt.append("- Category: ").append(task.getTaskCategory()).append("\n");
            prompt.append("- Priority: ").append(task.getTaskPriority()).append("\n");
            prompt.append("- Description: ").append(task.getTaskDescription()).append("\n");
            prompt.append("- Current Progress: ").append(task.getTaskCurrentProgress()).append("%\n");

            if (task.getTaskDeadline() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                prompt.append("- Deadline: ").append(dateFormat.format(task.getTaskDeadline())).append("\n");
            } else {
                prompt.append("- Deadline: Not specified\n");
            }

            if (task.getSubTasks() != null && !task.getSubTasks().isEmpty()) {
                prompt.append("- Subtasks:\n");
                for (String subtask : task.getSubTasks()) {
                    prompt.append("  * ").append(subtask).append("\n");
                }
            }

            prompt.append("\n");
        }

        // User's specific instructions, if any
        if (userPrompt != null && !userPrompt.trim().isEmpty()) {
            prompt.append("The user has provided the following additional instructions for this session:\n");
            prompt.append("\"").append(userPrompt).append("\"\n\n");
        }


        prompt.append("Based on this information, please create a session plan with the following components:\n\n");
        prompt.append("1. sessionGoal: A specific, descriptive goal for this work session. This should be detailed, ");
        prompt.append("motivational, and clearly state what the user will accomplish in this session. The goal should be ");
        prompt.append("realistic for a single focused work period (1-3 hours).\n\n");

        prompt.append("2. tasksOperated: A list of 1-4 task names from the provided list that align with the session goal. ");
        prompt.append("These tasks should be related and make sense to work on together. ");
        prompt.append("Include only the task names, exactly as they appear in the original list.\n\n");

        prompt.append("3. sessionOutcome: A detailed description of what the user will have accomplished by the end of the session, ");
        prompt.append("written in the first person as if the user is describing their accomplishments. ");
        prompt.append("Use phrases like 'I have completed...', 'I have ensured...', 'I have made progress on...', and 'I will have finished...'. ");
        prompt.append("This should specify concrete outcomes and progress measures for each selected task.\n\n");

        prompt.append("4. duration: An estimated duration in whole minutes (between 10 to 120 minutes) for the session. This should be a realistic estimate based on the goal and tasks. \n\n");

        prompt.append("Respond with a JSON object containing these four fields. The response should be properly formatted JSON without any markdown or additional explanations.");
        return prompt.toString();
    }

    /**
     * Extract and format the session data from AI response
     */
    private Map<String, Object> extractSessionData(String aiResponse) throws JsonProcessingException {
        // Extract JSON from response
        String jsonResponse = extractJsonFromResponse(aiResponse);

        // Parse JSON
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> sessionData = objectMapper.readValue(jsonResponse, Map.class);

        // Ensure all required fields exist
        if (!sessionData.containsKey("sessionGoal")) {
            sessionData.put("sessionGoal", "Focus on high-priority tasks to make significant progress");
        }

        if (!sessionData.containsKey("tasksOperated")) {
            sessionData.put("tasksOperated", new ArrayList<String>());
        }

        if (!sessionData.containsKey("sessionOutcome")) {
            sessionData.put("sessionOutcome", "Complete selected tasks and advance toward your goals");
        }

        // Add default duration
        if (!sessionData.containsKey("duration")) {
            sessionData.put("duration", 60); // Default duration of 60 minutes
        }

        return sessionData;
    }

    /**
     * Extract JSON from AI response, handling various formats
     */
    private String extractJsonFromResponse(String aiResponse) {
        // Clean up the response
        aiResponse = aiResponse.trim();

        // If response already looks like JSON, return it
        if ((aiResponse.startsWith("{") && aiResponse.endsWith("}")) ||
                (aiResponse.startsWith("[") && aiResponse.endsWith("]"))) {
            return aiResponse;
        }

        // Try to extract JSON from code blocks
        if (aiResponse.contains("```json")) {
            int start = aiResponse.indexOf("```json") + 7;
            int end = aiResponse.indexOf("```", start);
            if (end != -1) {
                return aiResponse.substring(start, end).trim();
            }
        }

        // Try with regular code blocks
        if (aiResponse.contains("```")) {
            int start = aiResponse.indexOf("```") + 3;
            int end = aiResponse.indexOf("```", start);
            if (end != -1) {
                String possibleJson = aiResponse.substring(start, end).trim();
                if ((possibleJson.startsWith("{") && possibleJson.endsWith("}")) ||
                        (possibleJson.startsWith("[") && possibleJson.endsWith("]"))) {
                    return possibleJson;
                }
            }
        }

        // Try to find JSON object directly
        int jsonStart = aiResponse.indexOf("{");
        int jsonEnd = aiResponse.lastIndexOf("}");

        if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
            return aiResponse.substring(jsonStart, jsonEnd + 1);
        }

        // If no JSON found, create a default response
        return "{\"sessionGoal\": \"Complete high-priority tasks\", " +
                "\"tasksOperated\": [], " +
                "\"duration\": 30, " +
                "\"sessionOutcome\": \"Make progress on important work\"}";
    }

    public String generateGenericInsight(Object data, String context, String analysisType) {
        try {
            // Convert the data to a string representation for the prompt
            String dataRepresentation = convertDataToString(data);

            // Build the AI prompt
            StringBuilder prompt = new StringBuilder();
            prompt.append("You are an expert data analyst and productivity assistant. ");
            prompt.append("Your task is to analyze the following data and provide concise, actionable insights. ");

            // Add context if provided
            if (context != null && !context.isEmpty()) {
                prompt.append("Context: ").append(context).append(". ");
            }

            // Customize analysis based on type
            switch (analysisType.toLowerCase()) {
                case "summary":
                    prompt.append("Provide a brief summary of the key points in this data. ");
                    prompt.append("Focus on the most important elements and overall patterns. ");
                    prompt.append("Keep your response under 150 words. ");
                    break;
                case "detailed":
                    prompt.append("Provide a detailed analysis of this data. ");
                    prompt.append("Identify patterns, outliers, and actionable insights. ");
                    prompt.append("Organize your response into clear sections with bullet points where appropriate. ");
                    break;
                case "recommendations":
                    prompt.append("Based on this data, provide 3-5 specific recommendations. ");
                    prompt.append("Each recommendation should be actionable and directly tied to the data. ");
                    break;
                case "metrics":
                    prompt.append("Extract and highlight the key metrics from this data. ");
                    prompt.append("Compare any values to benchmarks if possible. ");
                    break;
                default: // standard
                    prompt.append("Analyze this data and provide a balanced overview of key insights. ");
                    prompt.append("Include both strengths and areas for improvement. ");
                    prompt.append("Keep your response concise and focused on actionable information. ");
            }

            // Add the data
            prompt.append("\n\nData to analyze:\n").append(dataRepresentation);

            // Add final instructions
            prompt.append("\n\nRespond with only the analysis text - no additional formatting, introduction, ");
            prompt.append("or explanation about what you're doing. The response should be in plain text format ");
            prompt.append("that can be directly presented to the user. Aim for clarity and brevity while capturing the essence of the data.");

            // Generate AI response
            String aiResponse = generateResponse(prompt.toString());

            // Extract and clean the text content
            return extractTextContent(aiResponse);
        } catch (Exception e) {
            return "Unable to analyze the provided data: " + e.getMessage();
        }
    }

    /**
     * Converts different data types to string representation for analysis
     */
    private String convertDataToString(Object data) {
        if (data == null) {
            return "No data provided";
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            // Format dates nicely
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            objectMapper.setDateFormat(dateFormat);

            // Special handling based on data type
            if (data instanceof List<?>) {
                List<?> dataList = (List<?>) data;
                if (dataList.isEmpty()) {
                    return "Empty list provided";
                }

                // For small lists, provide more detailed representation
                if (dataList.size() <= 20) {
                    return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dataList);
                } else {
                    // For larger lists, provide sample and summary
                    StringBuilder builder = new StringBuilder();
                    builder.append("List with ").append(dataList.size()).append(" items. Sample items:\n");

                    // Add first 5 items
                    for (int i = 0; i < Math.min(5, dataList.size()); i++) {
                        builder.append(objectMapper.writeValueAsString(dataList.get(i))).append("\n");
                    }

                    // Add last 5 items if list is large enough
                    if (dataList.size() > 10) {
                        builder.append("...\n");
                        for (int i = Math.max(5, dataList.size() - 5); i < dataList.size(); i++) {
                            builder.append(objectMapper.writeValueAsString(dataList.get(i))).append("\n");
                        }
                    }

                    return builder.toString();
                }
            } else if (data instanceof Map) {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
            } else {
                // Default to JSON string representation
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
            }
        } catch (Exception e) {
            // Fallback to basic string representation
            return "Data type: " + data.getClass().getSimpleName() + "\nString representation: " + data.toString();
        }
    }
}

