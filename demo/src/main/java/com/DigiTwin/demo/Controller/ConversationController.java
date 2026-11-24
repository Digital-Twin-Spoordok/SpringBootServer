package com.DigiTwin.demo.Controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.DigiTwin.demo.Model.Conversation;
import com.DigiTwin.demo.Repository.ConversationRepository;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
    private final ConversationRepository repository;
    public ConversationController(ConversationRepository repository) { this.repository = repository; }

    @GetMapping public List<Conversation> findAll() { return repository.findAll(); }
    @GetMapping("/{id}") public Conversation findById(@PathVariable String id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Niet gevonden."));
    }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Conversation create(@RequestBody Conversation item) {
        item.setId(null);
        return repository.save(item);
    }
    @PutMapping("/{id}") public Conversation update(@PathVariable String id, @RequestBody Conversation item) {
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
