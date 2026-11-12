package com.DigiTwin.demo.Controller;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/assistant")
public class AssistantController {
    private final WebClient webClient;
    private final String model;

    public AssistantController(WebClient.Builder builder,
            @Value("${ollama.base-url:http://localhost:11434}") String baseUrl,
            @Value("${ollama.model:qwen3.5:4b}") String model) {
        this.webClient = builder.baseUrl(baseUrl).build();
        this.model = model;
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody ChatRequest request) {
        if (request == null || request.message == null || request.message.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bericht mag niet leeg zijn.");
        }
        if (request.message.length() > 4000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bericht mag maximaal 4.000 tekens bevatten.");
        }
        Map<String, Object> payload = Map.of("model", model, "stream", false,
                "messages", List.of(Map.of("role", "user", "content", request.message.trim())));
        try {
            Map response = webClient.post().uri("/api/chat").bodyValue(payload)
                    .retrieve().bodyToMono(Map.class).block(Duration.ofMinutes(3));
            Object messageValue = response == null ? null : response.get("message");
            Object answerValue = messageValue instanceof Map ? ((Map) messageValue).get("content") : null;
            String answer = answerValue == null ? null : answerValue.toString();
            if (answer == null || answer.trim().isEmpty()) throw new IllegalStateException("Ollama gaf geen antwoord.");
            return Map.of("answer", answer, "model", model);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Ollama is niet bereikbaar. Start Ollama en controleer model " + model + ".", ex);
        }
    }

    public static class ChatRequest { public String message; }
}
