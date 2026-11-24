package com.DigiTwin.demo.Controller;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.DigiTwin.demo.Model.DevSettings;
import com.DigiTwin.demo.Repository.DevSettingsRepository;
import com.DigiTwin.demo.Service.AIService;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {
    private final DevSettingsRepository repository;
    private final AIService aiService;
    public SettingsController(DevSettingsRepository repository, AIService aiService) { this.repository=repository; this.aiService=aiService; }

    @GetMapping public DevSettings get() { return repository.findById("global").orElseGet(DevSettings::new); }

    @PutMapping public DevSettings update(@RequestBody DevSettings input) {
        DevSettings current=get();
        if(input.getModel()!=null && !input.getModel().isBlank()) current.setModel(input.getModel());
        if(input.getBaseUrl()!=null && !input.getBaseUrl().isBlank()) current.setBaseUrl(input.getBaseUrl());
        if(input.getTemperature()!=null) current.setTemperature(input.getTemperature());
        if(input.getMaxTokens()!=null) current.setMaxTokens(input.getMaxTokens());
        if(input.getSystemPrompt()!=null) current.setSystemPrompt(input.getSystemPrompt());
        current.setUpdatedAt(Instant.now());
        return repository.save(current);
    }

    @GetMapping("/prompt") public Map<String,String> prompt() {
        return Map.of("prompt", get().getSystemPrompt()==null ? "" : get().getSystemPrompt());
    }
    @PutMapping("/prompt") public Map<String,String> savePrompt(@RequestBody Map<String,String> body) {
        DevSettings s=get(); s.setSystemPrompt(body.getOrDefault("prompt","")); repository.save(s);
        return Map.of("prompt",s.getSystemPrompt());
    }
    @PostMapping("/test") public Map<String,Object> test() {
        try { return aiService.testConnection(); }
        catch(Exception e) { throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Ollama is niet bereikbaar.",e); }
    }
}
