package com.DigiTwin.demo.Model;

import java.time.Instant;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("messages")
public class Message {
    @Id private String id;
    private String conversationId;
    private String role;
    private String content;
    private Map<String,Object> actions;
    private Instant createdAt = Instant.now();

    public Message() {}
    public String getId(){return id;} public void setId(String v){id=v;}
    public String getConversationId(){return conversationId;} public void setConversationId(String v){conversationId=v;}
    public String getRole(){return role;} public void setRole(String v){role=v;}
    public String getContent(){return content;} public void setContent(String v){content=v;}
    public Map<String,Object> getActions(){return actions;} public void setActions(Map<String,Object> v){actions=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
}
