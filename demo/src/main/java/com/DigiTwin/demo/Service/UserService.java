package com.DigiTwin.demo.Service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.DigiTwin.demo.Model.*;
import com.DigiTwin.demo.Repository.UserRepository;

@Service
public class UserService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    public UserService(UserRepository users, PasswordEncoder encoder) { this.users=users; this.encoder=encoder; }

    public User register(String name, String email, String password) {
        if (name == null || name.isBlank() || email == null || email.isBlank() || password == null || password.length() < 8)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Naam, e-mail en wachtwoord (minimaal 8 tekens) zijn verplicht.");
        if (users.existsByEmailIgnoreCase(email)) throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mailadres is al in gebruik.");
        return users.save(new User(name.trim(), email.trim().toLowerCase(), encoder.encode(password), UserRole.USER));
    }

    public User authenticate(String email, String password) {
        User user = users.findByEmailIgnoreCase(email == null ? "" : email).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ongeldige inloggegevens."));
        if (!user.isEnabled() || !encoder.matches(password, user.getPasswordHash()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ongeldige inloggegevens.");
        return user;
    }

    public List<User> findAll() { return users.findAll(); }
    public User find(String id) { return users.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gebruiker niet gevonden.")); }
    public User save(User user) { user.setPasswordHash(user.getPasswordHash()); return users.save(user); }
    public void delete(String id) { if (!users.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND); users.deleteById(id); }
}
