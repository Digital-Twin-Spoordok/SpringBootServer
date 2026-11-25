
package com.DigiTwin.demo.Controller;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.DigiTwin.demo.Model.Project;
import com.DigiTwin.demo.Model.Scenario;
import com.DigiTwin.demo.Repository.ProjectRepository;
import com.DigiTwin.demo.Repository.ScenarioRepository;

@RestController
@RequestMapping("/api")
public class ScenarioController {

    private final ScenarioRepository scenarioRepository;
    private final ProjectRepository projectRepository;

    public ScenarioController(
            ScenarioRepository scenarioRepository,
            ProjectRepository projectRepository) {
        this.scenarioRepository = scenarioRepository;
        this.projectRepository = projectRepository;
    }

    private Project getOwnedProject(
            String projectId,
            Authentication authentication) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project niet gevonden."));

        if (!authentication.getName().equals(project.getOwnerId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Je hebt geen toegang tot dit project.");
        }

        return project;
    }

    private Scenario getOwnedScenario(
            String scenarioId,
            Authentication authentication) {

        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Scenario niet gevonden."));

        getOwnedProject(scenario.getProjectId(), authentication);

        return scenario;
    }

    // Scenario's ophalen binnen een project
    @GetMapping("/projects/{projectId}/scenarios")
    public List<Scenario> findByProject(
            @PathVariable String projectId,
            Authentication authentication) {

        getOwnedProject(projectId, authentication);

        return scenarioRepository.findByProjectId(projectId);
    }

    // Scenario aanmaken
    @PostMapping("/projects/{projectId}/scenarios")
    @ResponseStatus(HttpStatus.CREATED)
    public Scenario create(
            @PathVariable String projectId,
            @RequestBody Scenario scenario,
            Authentication authentication) {

        getOwnedProject(projectId, authentication);

        scenario.setId(null);
        scenario.setProjectId(projectId);
        scenario.setActiveVersionId(null);
        scenario.setCreatedAt(Instant.now());
        scenario.setUpdatedAt(Instant.now());

        if (scenario.getName() == null ||
                scenario.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Een scenarionaam is verplicht.");
        }

        return scenarioRepository.save(scenario);
    }


    
    @PostMapping("/projects/{projectId}/scenarios/{id}/duplicate")
    @ResponseStatus(HttpStatus.CREATED)
    public Scenario duplicate(
            @PathVariable String projectId,
            @RequestBody Scenario scenario,
            Authentication auth) {

        Scenario original = getOwnedScenario(scenario.getId(), auth);

        Scenario copy = new Scenario();

        copy.setProjectId(original.getProjectId());
        copy.setName(original.getName() + " (kopie)");
        copy.setDescription(original.getDescription());
        copy.setStatus("DRAFT");
        copy.setActiveVersionId(null);
        copy.setCreatedAt(Instant.now());
        copy.setUpdatedAt(Instant.now());

        return scenarioRepository.save(copy);
    }



    // Scenario wijzigen
    @PutMapping("/scenarios/{id}")
    public Scenario update(
            @PathVariable String id,
            @RequestBody Scenario updated,
            Authentication authentication) {

        Scenario existing = getOwnedScenario(id, authentication);

        if (updated.getName() == null ||
                updated.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Een scenarionaam is verplicht.");
        }

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setStatus(updated.getStatus());
        existing.setUpdatedAt(Instant.now());

        return scenarioRepository.save(existing);
    }

    // Scenario verwijderen
    @DeleteMapping("/scenarios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable String id,
            Authentication authentication) {

        Scenario scenario = getOwnedScenario(id, authentication);

        scenarioRepository.delete(scenario);
    }
}
