package com.DigiTwin.demo.Controller;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api/status")
public class DevStatusController {
    private final MongoTemplate mongo;
    private final WebClient ollama;
    @Value("${ollama.model:qwen3.5:4b}") private String model;

    public DevStatusController(MongoTemplate mongo, WebClient.Builder builder,
        @Value("${ollama.base-url:http://host.docker.internal:11434}") String url) {
        this.mongo=mongo; this.ollama=builder.baseUrl(url).build();
    }
    @GetMapping public Map<String,Object> status() {
        Map<String,Object> result=new LinkedHashMap<>();
        result.put("backend","Online");
        try { mongo.getDb().runCommand(new Document("ping",1)); result.put("mongodb","Connected"); }
        catch(Exception e) { result.put("mongodb","Offline"); }
        try { ollama.get().uri("/api/tags").retrieve().bodyToMono(Map.class).block(Duration.ofSeconds(4)); result.put("ollama","Online"); }
        catch(Exception e) { result.put("ollama","Offline"); }
        result.put("model",model);
        return result;
    }
}
