package com.DigiTwin.demo.Controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.DigiTwin.demo.Model.ProjectMember;
import com.DigiTwin.demo.Repository.ProjectMemberRepository;

@RestController
@RequestMapping("/api/project-members")
public class ProjectMemberController {
    private final ProjectMemberRepository repository;
    public ProjectMemberController(ProjectMemberRepository repository) { this.repository = repository; }

    @GetMapping public List<ProjectMember> findAll() { return repository.findAll(); }
    @GetMapping("/{id}") public ProjectMember findById(@PathVariable String id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Niet gevonden."));
    }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ProjectMember create(@RequestBody ProjectMember item) {
        item.setId(null);
        return repository.save(item);
    }
    @PutMapping("/{id}") public ProjectMember update(@PathVariable String id, @RequestBody ProjectMember item) {
        if (!repository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Niet gevonden.");
        item.setId(id);
        return repository.save(item);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        if (!repository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Niet gevonden.");
        repository.deleteById(id);
    }
}
