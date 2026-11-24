package com.DigiTwin.demo.Repository;

import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.DigiTwin.demo.Model.User;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmailIgnoreCase(String email); boolean existsByEmailIgnoreCase(String email);
}
