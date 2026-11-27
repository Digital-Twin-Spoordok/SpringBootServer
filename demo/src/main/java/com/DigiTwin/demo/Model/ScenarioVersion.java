
package com.DigiTwin.demo.Model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("scenario_versions")
@CompoundIndex(
    name = "scenario_version_unique",
    def = "{'scenarioId': 1, 'versionNumber': 1}",
    unique = true
)
public class ScenarioVersion {

    @Id
    private String id;

    private String scenarioId;
    private int versionNumber;

    private String name;
    private String description;

    private Instant createdAt = Instant.now();
    private String createdBy;

    private List<ScenarioObject> objects = new ArrayList<>();

    public ScenarioVersion() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<ScenarioObject> getObjects() {
        return objects;
    }

    public void setObjects(List<ScenarioObject> objects) {
        this.objects = objects;
    }
}
