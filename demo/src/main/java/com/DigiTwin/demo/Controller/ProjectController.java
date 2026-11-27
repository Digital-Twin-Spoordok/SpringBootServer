
package com.DigiTwin.demo.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.DigiTwin.demo.Model.Project;
import com.DigiTwin.demo.Repository.ProjectRepository;

@RestController
@CrossOrigin(origins = {"http://localhost:5500/", "http://localhost:63342"})
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectRepository repository;

    public ProjectController(ProjectRepository repository) {
        this.repository = repository;
    }

    private Project getOwnedProject(
            String id,
            Authentication authentication) {

        Project project = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project niet gevonden."));

        if (!authentication.getName().equals(project.getOwnerId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Je hebt geen toegang tot dit project.");
        }

        return project;
    }

    // Alle eigen projecten ophalen
    @GetMapping
    public List<Project> findAll(Authentication authentication) {
        System.out.println("Ingelogde user ID: " + authentication.getName());
        return repository.findByOwnerId(authentication.getName());
    }

    // Eén project ophalen
    @GetMapping("/{id}")
    public Project findById(
            @PathVariable String id,
            Authentication authentication) {

        return getOwnedProject(id, authentication);
    }

    // Project aanmaken
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Project create(
            @RequestBody Project item,
            Authentication authentication) {

        if (item.getName() == null || item.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Een projectnaam is verplicht.");
        }

        item.setId(null);
        item.setOwnerId(authentication.getName());

        return repository.save(item);
    }

    // Project wijzigen
    @PutMapping("/{id}")
    public Project update(
            @PathVariable String id,
            @RequestBody Project updated,
            Authentication authentication) {

        Project existing = getOwnedProject(id, authentication);

        if (updated.getName() == null ||
                updated.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Een projectnaam is verplicht.");
        }

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setStatus(updated.getStatus());
        existing.setDefaultAIConfigurationID(
                updated.getDefaultAIConfigurationID());

        return repository.save(existing);
    }

    // Project verwijderen
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable String id,
            Authentication authentication) {

        Project project = getOwnedProject(id, authentication);

        repository.delete(project);
    }
}
