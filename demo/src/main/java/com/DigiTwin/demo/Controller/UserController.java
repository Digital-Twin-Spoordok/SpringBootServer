package com.DigiTwin.demo.Controller;

import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.DigiTwin.demo.Model.User;
import com.DigiTwin.demo.Repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository users;
    public UserController(UserRepository users) { this.users=users; }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Map<String,Object>> all() { return users.findAll().stream().map(this::safe).toList(); }

    @GetMapping("/me")
    public Map<String,Object> me(org.springframework.security.core.Authentication auth) {
        User user = users.findById(auth.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return safe(user);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String,Object> get(@PathVariable String id) {
        return safe(users.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String,Object> update(@PathVariable String id, @RequestBody UpdateUser request) {
        User u=users.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(request.name!=null) u.setName(request.name);
        if(request.role!=null) u.setRole(request.role);
        if(request.enabled!=null) u.setEnabled(request.enabled);
        if (request.email != null) {
            String email = request.email.trim().toLowerCase();

            if (email.isBlank()) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "E-mailadres mag niet leeg zijn."
                );
            }

            users.findByEmailIgnoreCase(email).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Dit e-mailadres is al in gebruik."
                    );
                }
            });

            u.setEmail(email);
        }
        return safe(users.save(u));
    }

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable String id) {
        if(!users.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        users.deleteById(id);
    }

    private Map<String,Object> safe(User u) {
        Map<String,Object> m=new LinkedHashMap<>();
        m.put("id",u.getId()); m.put("name",u.getName()); m.put("email",u.getEmail());
        m.put("role",u.getRole()); m.put("enabled",u.isEnabled()); m.put("createdAt",u.getCreatedAt());
        return m;
    }
    public static class UpdateUser {
        public String name;
        public String email;
        public com.DigiTwin.demo.Model.UserRole role;
        public Boolean enabled;
    }
}
