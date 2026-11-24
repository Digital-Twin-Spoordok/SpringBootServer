package com.DigiTwin.demo.Controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.DigiTwin.demo.Model.Asset;
import com.DigiTwin.demo.Repository.AssetRepository;

@RestController
@RequestMapping("/api/assets")
public class AssetController {
    private final AssetRepository repository;
    public AssetController(AssetRepository repository) { this.repository = repository; }

    @GetMapping public List<Asset> findAll() { return repository.findAll(); }
    @GetMapping("/{id}") public Asset findById(@PathVariable String id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Niet gevonden."));
    }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Asset create(@RequestBody Asset item) {
        item.setId(null);
        return repository.save(item);
    }
    @PutMapping("/{id}") public Asset update(@PathVariable String id, @RequestBody Asset item) {
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
