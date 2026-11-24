package com.DigiTwin.demo.Repository;

import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.DigiTwin.demo.Model.Project;

public interface ProjectRepository extends MongoRepository<Project, String> {
    List<Project> findByOwnerId(String ownerId);
}
