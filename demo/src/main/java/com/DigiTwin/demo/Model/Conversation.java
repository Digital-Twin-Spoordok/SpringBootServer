package com.DigiTwin.demo.Model;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("conversations")
public class Conversation {
    @Id private String id;
    private String userId;
    private String title;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public Conversation() {}
    public String getId(){return id;} public void setId(String v){id=v;}
    public String getUserId(){return userId;} public void setUserId(String v){userId=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
    public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
}
