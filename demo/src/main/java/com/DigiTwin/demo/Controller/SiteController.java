package com.DigiTwin.demo.Controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.DigiTwin.demo.Model.Site;
import com.DigiTwin.demo.Repository.SiteRepository;

@RestController
@RequestMapping("/api/sites")
public class SiteController {
    private final SiteRepository repository;
    public SiteController(SiteRepository repository) { this.repository = repository; }

    @GetMapping public List<Site> findAll() { return repository.findAll(); }
    @GetMapping("/{id}") public Site findById(@PathVariable String id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Niet gevonden."));
    }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Site create(@RequestBody Site item) {
        item.setId(null);
        return repository.save(item);
    }
    @PutMapping("/{id}") public Site update(@PathVariable String id, @RequestBody Site item) {
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
