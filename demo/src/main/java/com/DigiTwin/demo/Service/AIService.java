
// package com.DigiTwin.demo.Service;

// import java.time.Duration;
// import java.util.List;
// import java.util.Map;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.core.io.Resource;
// import org.springframework.core.io.ResourceLoader;
// import org.springframework.stereotype.Service;
// import org.springframework.web.reactive.function.client.WebClient;

// import com.DigiTwin.demo.Tools.ToolDefinitions;

// @Service
// public class AIService {

//     private final WebClient webClient;
//     private final String model;
//     private final ResourceLoader resourceLoader;

//     public AIService(
//             WebClient.Builder builder,
//             ResourceLoader resourceLoader,
//             @Value("${ollama.base-url:http://host.docker.internal:11434}")
//             String baseUrl,
//             @Value("${ollama.model:qwen3.5:4b}")
//             String model) {

//         this.webClient = builder.baseUrl(baseUrl).build();
//         this.model = model;
//         this.resourceLoader = resourceLoader;
//     }

//     public Map<String, Object> askAI(String question) {

//         try {
//             Resource resource = resourceLoader.getResource(
//                 "classpath:Prompts/system-prompt.txt"
//             );

//             String systemPrompt = new String(
//                 resource.getInputStream().readAllBytes(),
//                 java.nio.charset.StandardCharsets.UTF_8
//             );

//             Map<String, Object> payload = Map.of(
//                 "model", model,
//                 "stream", false,

//                 "messages", List.of(
//                     Map.of(
//                         "role", "system",
//                         "content", systemPrompt
//                     ),
//                     Map.of(
//                         "role", "user",
//                         "content", question
//                     )
//                 ),

//                 "tools", ToolDefinitions.getTools()
//             );

//             return webClient.post()
//                 .uri("/api/chat")
//                 .bodyValue(payload)
//                 .retrieve()
//                 .bodyToMono(Map.class)
//                 .block(Duration.ofMinutes(3));

//         } catch (Exception ex) {
//             throw new RuntimeException(
//                 "Fout tijdens communicatie met Ollama", ex
//             );
//         }
//     }
// }

package com.DigiTwin.demo.Service;

import java.time.Duration;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.DigiTwin.demo.Model.DevSettings;
import com.DigiTwin.demo.Repository.DevSettingsRepository;
import com.DigiTwin.demo.Tools.ToolDefinitions;

@Service
public class AIService {
    private final WebClient.Builder builder;
    private final ResourceLoader resourceLoader;
    private final DevSettingsRepository settingsRepository;
    private final String configuredUrl;
    private final String configuredModel;

    public AIService(WebClient.Builder builder, ResourceLoader resourceLoader, DevSettingsRepository settingsRepository,
        @Value("${ollama.base-url:http://host.docker.internal:11434}") String baseUrl,
        @Value("${ollama.model:qwen3.5:4b}") String model) {
        this.builder=builder; this.resourceLoader=resourceLoader; this.settingsRepository=settingsRepository;
        this.configuredUrl=baseUrl; this.configuredModel=model;
    }

    public Map<String,Object> askAI(String question) {
        try {
            DevSettings settings=settingsRepository.findById("global").orElseGet(DevSettings::new);
            String prompt=settings.getSystemPrompt();
            if(prompt==null || prompt.isBlank()) {
                Resource resource=resourceLoader.getResource("classpath:Prompts/system-prompt.txt");
                prompt=resource.exists()?new String(resource.getInputStream().readAllBytes(),java.nio.charset.StandardCharsets.UTF_8):"Je bent een assistent voor een Digital Twin.";
            }
            String url=settings.getBaseUrl()==null || settings.getBaseUrl().isBlank()?configuredUrl:settings.getBaseUrl();
            String model=settings.getModel()==null || settings.getModel().isBlank()?configuredModel:settings.getModel();
            Map<String,Object> options=new HashMap<>();
            options.put("temperature",settings.getTemperature()==null?0.7:settings.getTemperature());
            options.put("num_predict",settings.getMaxTokens()==null?2048:settings.getMaxTokens());
            Map<String,Object> payload=new HashMap<>();
            payload.put("model",model); payload.put("stream",false); payload.put("options",options);
            payload.put("messages",List.of(Map.of("role","system","content",prompt),Map.of("role","user","content",question)));
            payload.put("tools",ToolDefinitions.getTools());
            return builder.baseUrl(url).build().post().uri("/api/chat").bodyValue(payload)
                .retrieve().bodyToMono(Map.class).block(Duration.ofMinutes(3));
        } catch(Exception e) { throw new RuntimeException("Fout tijdens communicatie met Ollama",e); }
    }

    public Map<String,Object> testConnection() {
        DevSettings s=settingsRepository.findById("global").orElseGet(DevSettings::new);
        String url=s.getBaseUrl()==null || s.getBaseUrl().isBlank()?configuredUrl:s.getBaseUrl();
        Map response=builder.baseUrl(url).build().get().uri("/api/tags").retrieve()
            .bodyToMono(Map.class).block(Duration.ofSeconds(8));
        return Map.of("connected",true,"models",response.getOrDefault("models",List.of()));
    }
}

