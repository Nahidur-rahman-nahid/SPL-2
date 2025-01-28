package com.TimeWise.controller;

import com.TimeWise.service.MistralAIService;
import com.TimeWise.utils.GenerateTaskRequestBody;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/ai/mistral")
public class    MistralAIController {

    private final MistralAIService mistralAIService;

    @PostMapping("/generate/tasks")
    public Map<String, Object> generateTasks(@RequestBody GenerateTaskRequestBody requestBody) {
        // Format the final deadline and current date for prompt
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(new java.util.Date());
        String finalDeadline = dateFormat.format(requestBody.getGoalDeadline());

        // Generate a well-crafted prompt
        String prompt = String.format(
                "You are an AI assistant helping with goal planning. The goal is '%s'. Create a list of tasks to achieve this goal. Each task must include: "
                        + "taskName, taskCategory, taskDescription, taskPriority (low, medium, high), "
                        + "taskGoal(same for all tasks the goal name being '%s') and taskDeadline. Ensure task deadlines start from '%s' and do not exceed '%s'. "
                        + "Respond in JSON format as a list of task objects.",requestBody.getGoalName(),
                requestBody.getGoalName(), currentDate, finalDeadline
        );

        // Call the service to generate tasks
        String response = mistralAIService.generateResponse(prompt);

        return Map.of("tasks", response);
    }
    @PostMapping("/generate/text")
    public String generateText(@PathVariable String prompt) {

        String totalPrompt = "You are an AI assistant helping with answering prompt to a user. The users prompt is the following: "+prompt;

        return mistralAIService.generateResponse(totalPrompt);
    }
}
