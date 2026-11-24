package com.DigiTwin.demo.Model;

import java.time.Instant;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("tool_executions")
public class ToolExecution {
    @Id private String id;
    private String conversationId;
    private String toolName;
    private Map<String,Object> arguments;
    private String result;
    private String status = "SUCCESS";
    private Instant createdAt = Instant.now();

    public ToolExecution() {}
    public String getId(){return id;} public void setId(String v){id=v;}
    public String getConversationId(){return conversationId;} public void setConversationId(String v){conversationId=v;}
    public String getToolName(){return toolName;} public void setToolName(String v){toolName=v;}
    public Map<String,Object> getArguments(){return arguments;} public void setArguments(Map<String,Object> v){arguments=v;}
    public String getResult(){return result;} public void setResult(String v){result=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
}
