package com.DigiTwin.demo.Model;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("dev_settings")
public class DevSettings {
    @Id private String id = "global";
    private String model = "qwen3.5:4b";
    private String baseUrl = "http://host.docker.internal:11434";
    private Double temperature = 0.7;
    private Integer maxTokens = 2048;
    private String systemPrompt;
    private Instant updatedAt = Instant.now();

    public String getId(){return id;} public void setId(String v){id=v;}
    public String getModel(){return model;} public void setModel(String v){model=v;}
    public String getBaseUrl(){return baseUrl;} public void setBaseUrl(String v){baseUrl=v;}
    public Double getTemperature(){return temperature;} public void setTemperature(Double v){temperature=v;}
    public Integer getMaxTokens(){return maxTokens;} public void setMaxTokens(Integer v){maxTokens=v;}
    public String getSystemPrompt(){return systemPrompt;} public void setSystemPrompt(String v){systemPrompt=v;}
    public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
}
