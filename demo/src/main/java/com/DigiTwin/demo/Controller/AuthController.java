package com.DigiTwin.demo.Controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;

import com.DigiTwin.demo.Model.User;
import com.DigiTwin.demo.Service.JwtService;
import com.DigiTwin.demo.Service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5500/", "http://localhost:63342"})
public class AuthController {
    private final UserService users;
    private final JwtService jwt;
    public AuthController(UserService users, JwtService jwt) { this.users=users; this.jwt=jwt; }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String,Object> register(@RequestBody AuthRequest request) {
        User user = users.register(request.name, request.email, request.password);
        return response(user);
    }

    @PostMapping("/login")
    public Map<String,Object> login(@RequestBody AuthRequest request) {
        return response(users.authenticate(request.email, request.password));
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Niet ingelogd."
            );
        }

        String role = authentication.getAuthorities()
            .stream()
            .findFirst()
            .map(authority -> authority.getAuthority().replace("ROLE_", ""))
            .orElse("USER");

        return Map.of(
            "email", authentication.getName(),
            "role", role
        );
    }

    private Map<String,Object> response(User user) {
        return Map.of("token", jwt.generate(user.getId(), user.getEmail(), user.getRole().name()),
            "user", Map.of("id", user.getId(), "name", user.getName(), "email", user.getEmail(), "role", user.getRole().name()));
    }
    public static class AuthRequest { public String name; public String email; public String password; }
}
