package com.DigiTwin.demo.Repository;

import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.DigiTwin.demo.Model.Message;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByConversationIdOrderByCreatedAtAsc(String conversationId);
}
