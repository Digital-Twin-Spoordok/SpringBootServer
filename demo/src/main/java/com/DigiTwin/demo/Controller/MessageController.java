package com.DigiTwin.demo.Controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.DigiTwin.demo.Model.Message;
import com.DigiTwin.demo.Repository.MessageRepository;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageRepository repository;
    public MessageController(MessageRepository repository) { this.repository = repository; }

    @GetMapping public List<Message> findAll() { return repository.findAll(); }
    @GetMapping("/{id}") public Message findById(@PathVariable String id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Niet gevonden."));
    }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Message create(@RequestBody Message item) {
        item.setId(null);
        return repository.save(item);
    }
    @PutMapping("/{id}") public Message update(@PathVariable String id, @RequestBody Message item) {
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
