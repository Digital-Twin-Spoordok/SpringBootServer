
package com.DigiTwin.demo.Model;

import java.util.ArrayList;
import java.util.List;

public class CreateScenarioVersionRequest {

    private String name;
    private String description;

    // De versie die de frontend denkt dat momenteel actief is.
    private String expectedActiveVersionId;

    private List<ScenarioObject> objects = new ArrayList<>();

    public CreateScenarioVersionRequest() {}

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

    public String getExpectedActiveVersionId() {
        return expectedActiveVersionId;
    }

    public void setExpectedActiveVersionId(String expectedActiveVersionId) {
        this.expectedActiveVersionId = expectedActiveVersionId;
    }

    public List<ScenarioObject> getObjects() {
        return objects;
    }

    public void setObjects(List<ScenarioObject> objects) {
        this.objects = objects;
    }
}
