package com.DigiTwin.demo.Repository;

import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.DigiTwin.demo.Model.Site;

public interface SiteRepository extends MongoRepository<Site, String> {
    List<Site> findByProjectId(String projectId);
}
