
package com.DigiTwin.demo.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.DigiTwin.demo.Tools.ToolDefinitions;

@Service
public class AIService {

    private final WebClient webClient;
    private final String model;
    private final ResourceLoader resourceLoader;

    public AIService(
            WebClient.Builder builder,
            ResourceLoader resourceLoader,
            @Value("${ollama.base-url:http://localhost:11434}")
            String baseUrl,
            @Value("${ollama.model:qwen3.5:4b}")
            String model) {

        this.webClient = builder.baseUrl(baseUrl).build();
        this.model = model;
        this.resourceLoader = resourceLoader;
    }

    public Map<String, Object> askAI(String question) {

        try {
            Resource resource = resourceLoader.getResource(
                "classpath:Prompts/system-prompt.txt"
            );

            String systemPrompt = new String(
                resource.getInputStream().readAllBytes(),
                java.nio.charset.StandardCharsets.UTF_8
            );

            Map<String, Object> payload = Map.of(
                "model", model,
                "stream", false,

                "messages", List.of(
                    Map.of(
                        "role", "system",
                        "content", systemPrompt
                    ),
                    Map.of(
                        "role", "user",
                        "content", question
                    )
                ),

                "tools", ToolDefinitions.getTools()
            );

            return webClient.post()
                .uri("/api/chat")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Map.class)
                .block(Duration.ofMinutes(3));

        } catch (Exception ex) {
            throw new RuntimeException(
                "Fout tijdens communicatie met Ollama", ex
            );
        }
    }
}
