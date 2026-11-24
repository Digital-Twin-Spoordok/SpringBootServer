package com.DigiTwin.demo.Repository;

import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.DigiTwin.demo.Model.ToolExecution;

public interface ToolExecutionRepository extends MongoRepository<ToolExecution, String> {
    List<ToolExecution> findByConversationIdOrderByCreatedAtDesc(String conversationId);
}
