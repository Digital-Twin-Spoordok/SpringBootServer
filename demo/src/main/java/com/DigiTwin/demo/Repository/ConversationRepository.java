package com.DigiTwin.demo.Repository;

import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.DigiTwin.demo.Model.Conversation;

public interface ConversationRepository extends MongoRepository<Conversation, String> {
    List<Conversation> findByUserIdOrderByUpdatedAtDesc(String userId);
}
