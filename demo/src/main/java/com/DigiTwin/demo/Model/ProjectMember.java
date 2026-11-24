package com.DigiTwin.demo.Model;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("project_members")
public class ProjectMember {
    @Id private String id;
    private String projectId;
    private String userId;
    private String role = "VIEWER";
    private Instant addedAt = Instant.now();

    public ProjectMember() {}
    public String getId(){return id;} public void setId(String v){id=v;}
    public String getProjectId(){return projectId;} public void setProjectId(String v){projectId=v;}
    public String getUserId(){return userId;} public void setUserId(String v){userId=v;}
    public String getRole(){return role;} public void setRole(String v){role=v;}
    public Instant getAddedAt(){return addedAt;} public void setAddedAt(Instant v){addedAt=v;}
}
