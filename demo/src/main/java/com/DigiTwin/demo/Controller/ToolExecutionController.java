package com.DigiTwin.demo.Controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.DigiTwin.demo.Model.ToolExecution;
import com.DigiTwin.demo.Repository.ToolExecutionRepository;

@RestController
@RequestMapping("/api/tool-executions")
public class ToolExecutionController {
    private final ToolExecutionRepository repository;
    public ToolExecutionController(ToolExecutionRepository repository) { this.repository = repository; }

    @GetMapping public List<ToolExecution> findAll() { return repository.findAll(); }
    @GetMapping("/{id}") public ToolExecution findById(@PathVariable String id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Niet gevonden."));
    }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ToolExecution create(@RequestBody ToolExecution item) {
        item.setId(null);
        return repository.save(item);
    }
    @PutMapping("/{id}") public ToolExecution update(@PathVariable String id, @RequestBody ToolExecution item) {
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
