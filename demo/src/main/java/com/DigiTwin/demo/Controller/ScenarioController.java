
package com.DigiTwin.demo.Controller;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.DigiTwin.demo.Model.CreateScenarioVersionRequest;
import com.DigiTwin.demo.Model.Project;
import com.DigiTwin.demo.Model.Scenario;
import com.DigiTwin.demo.Model.ScenarioVersion;
import com.DigiTwin.demo.Repository.ProjectRepository;
import com.DigiTwin.demo.Repository.ScenarioRepository;
import com.DigiTwin.demo.Service.ScenarioVersionService;

@RestController
@RequestMapping("/api")
public class ScenarioController {

    private final ScenarioRepository scenarioRepository;
    private final ProjectRepository projectRepository;
    private final ScenarioVersionService versionService;

    public ScenarioController(
            ScenarioRepository scenarioRepository,
            ProjectRepository projectRepository,
            ScenarioVersionService versionService) {

        this.scenarioRepository = scenarioRepository;
        this.projectRepository = projectRepository;
        this.versionService = versionService;
    }

    private Project getOwnedProject(
            String projectId,
            Authentication authentication) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project niet gevonden."));

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
                        HttpStatus.NOT_FOUND,
                        "Scenario niet gevonden."));

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

    // Scenario aanmaken inclusief eerste versie
    @PostMapping("/projects/{projectId}/scenarios")
    @ResponseStatus(HttpStatus.CREATED)
    public Scenario create(
            @PathVariable String projectId,
            @RequestBody Scenario scenario,
            Authentication authentication) {

        getOwnedProject(projectId, authentication);

        if (scenario.getName() == null ||
                scenario.getName().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Een scenarionaam is verplicht.");
        }

        scenario.setId(null);
        scenario.setProjectId(projectId);
        scenario.setActiveVersionId(null);
        scenario.setCreatedAt(Instant.now());
        scenario.setUpdatedAt(Instant.now());

        Scenario savedScenario = scenarioRepository.save(scenario);

        versionService.createInitialVersion(
                savedScenario,
                authentication.getName());

        return scenarioRepository.findById(savedScenario.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Scenario niet gevonden."));
    }

    // Alle versies ophalen
    @GetMapping("/scenarios/{id}/versions")
    public List<ScenarioVersion> getVersions(
            @PathVariable String id,
            Authentication authentication) {

        Scenario scenario = getOwnedScenario(id, authentication);

        return versionService.getVersions(scenario.getId());
    }

    // Actieve versie ophalen
    @GetMapping("/scenarios/{id}/active-version")
    public ScenarioVersion getActiveVersion(
            @PathVariable String id,
            Authentication authentication) {

        Scenario scenario = getOwnedScenario(id, authentication);

        return versionService.getActiveVersion(scenario);
    }

    // Specifieke versie ophalen
    @GetMapping("/scenarios/{id}/versions/{versionId}")
    public ScenarioVersion getVersion(
            @PathVariable String id,
            @PathVariable String versionId,
            Authentication authentication) {

        Scenario scenario = getOwnedScenario(id, authentication);

        return versionService.getVersion(
                scenario.getId(),
                versionId);
    }

    // Nieuwe versie opslaan
    @PostMapping("/scenarios/{id}/versions")
    @ResponseStatus(HttpStatus.CREATED)
    public ScenarioVersion createVersion(
            @PathVariable String id,
            @RequestBody CreateScenarioVersionRequest request,
            Authentication authentication) {

        Scenario scenario = getOwnedScenario(id, authentication);

        return versionService.createVersion(
                scenario,
                request,
                authentication.getName());
    }

    // Oude versie herstellen als nieuwe versie
    @PostMapping("/scenarios/{id}/versions/{versionId}/restore")
    @ResponseStatus(HttpStatus.CREATED)
    public ScenarioVersion restoreVersion(
            @PathVariable String id,
            @PathVariable String versionId,
            Authentication authentication) {

        Scenario scenario = getOwnedScenario(id, authentication);

        return versionService.restoreVersion(
                scenario,
                versionId,
                authentication.getName());
    }

    // Versie dupliceren
    @PostMapping("/scenarios/{id}/versions/{versionId}/duplicate")
    @ResponseStatus(HttpStatus.CREATED)
    public ScenarioVersion duplicateVersion(
            @PathVariable String id,
            @PathVariable String versionId,
            Authentication authentication) {

        Scenario scenario = getOwnedScenario(id, authentication);

        return versionService.duplicateVersion(
                scenario,
                versionId,
                authentication.getName());
    }

    // Versie aanpassen
    @PutMapping("/scenarios/{id}/versions/{versionId}")
    public ScenarioVersion updateVersion(
            @PathVariable String id,
            @PathVariable String versionId,
            @RequestBody UpdateVersionRequest request,
            Authentication authentication) {

        Scenario scenario = getOwnedScenario(id, authentication);

        return versionService.updateVersion(
                scenario.getId(),
                versionId,
                request.getName(),
                request.getDescription());
    }

    // Versie verwijderen
    @DeleteMapping("/scenarios/{id}/versions/{versionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVersion(
            @PathVariable String id,
            @PathVariable String versionId,
            Authentication authentication) {

        Scenario scenario = getOwnedScenario(id, authentication);

        versionService.deleteVersion(scenario, versionId);
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

    // Requestmodel voor het aanpassen van een versie
    public static class UpdateVersionRequest {

        private String name;
        private String description;

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
    }
}
