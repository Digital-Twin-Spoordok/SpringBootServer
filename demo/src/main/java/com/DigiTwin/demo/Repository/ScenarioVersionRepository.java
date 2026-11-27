
package com.DigiTwin.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.DigiTwin.demo.Model.ScenarioVersion;

public interface ScenarioVersionRepository
        extends MongoRepository<ScenarioVersion, String> {

    List<ScenarioVersion> findByScenarioIdOrderByVersionNumberDesc(
            String scenarioId
    );

    Optional<ScenarioVersion> findByScenarioIdAndVersionNumber(
            String scenarioId,
            int versionNumber
    );

    Optional<ScenarioVersion> findByScenarioIdAndId(
            String scenarioId,
            String id
    );

    Optional<ScenarioVersion> findFirstByScenarioIdOrderByVersionNumberDesc(
            String scenarioId
    );
}
