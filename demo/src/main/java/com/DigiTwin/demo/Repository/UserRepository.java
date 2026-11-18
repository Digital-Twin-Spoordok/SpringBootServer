package com.DigiTwin.demo.Repository;

import com.DigiTwin.demo.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

}

