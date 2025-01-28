package com.TimeWise.service;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.MistralAiChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class MistralAIService {

    private final MistralAiChatModel chatModel;

    public MistralAIService(
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
}

