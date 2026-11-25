
package com.DigiTwin.demo.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.DigiTwin.demo.Model.Scenario;

public interface ScenarioRepository
        extends MongoRepository<Scenario, String> {

    List<Scenario> findByProjectId(String projectId);
}
