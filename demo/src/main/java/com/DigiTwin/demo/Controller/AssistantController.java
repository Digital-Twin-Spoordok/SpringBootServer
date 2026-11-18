
package com.DigiTwin.demo.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.DigiTwin.demo.Service.AIService;
import com.DigiTwin.demo.Service.ToolExecutor;

@CrossOrigin(origins = "http://127.0.0.1:5500/")
@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    private final AIService aiService;
    private final ToolExecutor toolExecutor;

    public AssistantController(
            AIService aiService,
            ToolExecutor toolExecutor) {

        this.aiService = aiService;
        this.toolExecutor = toolExecutor;
    }

    @PostMapping("/chat")
    public Map<String, Object> chat(
            @RequestBody ChatRequest request) {

        if (request == null
                || request.message == null
                || request.message.trim().isEmpty()) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Bericht mag niet leeg zijn."
            );
        }

        if (request.message.length() > 400000) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Bericht mag maximaal 4.000 tekens bevatten."
            );
        }

        try {

            // 1. Vraag naar AI sturen
            Map<String, Object> aiResponse = aiService.askAI(request.message.trim());

                System.out.println("========== OLLAMA RESPONSE ==========");
                System.out.println(aiResponse);
                System.out.println("=====================================");

            if (aiResponse == null) {
                throw new IllegalStateException(
                    "Ollama gaf geen response."
                );
            }

            // 2. Controleren op tool calls
            List<Map<String, Object>> actions = toolExecutor.processToolCalls(aiResponse);

                System.out.println("========== PROCESSED ACTIONS ==========");
                System.out.println(actions);
                System.out.println("=======================================");

            // 3. Gewone AI-tekst ophalen
            Object messageValue = aiResponse.get("message");

            String answer = "";
            String thinking = "";
            String toolCall = "";

            if (messageValue instanceof Map) {
                Map<?, ?> message = (Map<?, ?>) messageValue;
                System.err.println(message);
                Object contentValue = message.get("content");
                Object thinkingValue = message.get("thinking");
                Object ToolCallValue = message.get("tool_calls");

                if (contentValue instanceof String) {
                    answer = ((String) contentValue).trim();
                }

                if (thinkingValue instanceof String) {
                    thinking = ((String) thinkingValue).trim();
                }

                if (ToolCallValue instanceof String) {
                    toolCall = ((String) ToolCallValue).trim();
                }
            }

            // Geef een bevestiging als er wel een actie is uitgevoerd,
            // maar Qwen geen normaal tekstantwoord heeft gegeven.
            if (answer.isEmpty() && !actions.isEmpty()) {
                answer = "Ik heb de opdracht uitgevoerd!";
            }

            // 4. Response naar frontend
            return Map.of(
                "answer", answer,
                "thinking", thinking,
                "actions", actions,
                "ToolCalls", toolCall,
                "model", "ollama"
            );

        } catch (Exception ex) {

            throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Er is iets misgegaan bij de AI-verwerking.",
                ex
            );
        }
    }

    public static class ChatRequest {
        public String message;
    }
}
