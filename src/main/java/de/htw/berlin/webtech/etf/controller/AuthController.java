package de.htw.berlin.webtech.etf.controller;

import de.htw.berlin.webtech.etf.domain.entity.User;
import de.htw.berlin.webtech.etf.repository.UserRepository;
import de.htw.berlin.webtech.etf.service.auth.JwtService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller fuer Authentifizierung (Login und Registrierung).
 * Alle Endpoints unter /api/auth/ sind oeffentlich zugaenglich.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Registrierung eines neuen Users.
     * Prueft ob Email bereits existiert und hasht das Passwort.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Validate email (null/blank) and normalize
        String rawEmail = request.email();
        if (rawEmail == null || rawEmail.isBlank()) {
            return ResponseEntity.badRequest().body(error("Email ist erforderlich"));
        }
        String email = rawEmail.trim().toLowerCase();

        // Validate password
        if (request.password() == null || request.password().length() < 6) {
            return ResponseEntity.badRequest().body(error("Passwort muss mindestens 6 Zeichen haben"));
        }

        // Check existence using normalized email
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(error("Email ist bereits registriert"));
        }

        // Create and save new user with normalized email
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));

        User savedUser;
        try {
            savedUser = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            // Defensive: in case a unique constraint race occurs in DB
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(error("Email ist bereits registriert"));
        }

        // JWT-Token generieren
        String token = jwtService.generateToken(savedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse(token, savedUser));
    }

    /**
     * Login eines bestehenden Users.
     * Prueft Email und Passwort, gibt JWT zurueck.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Basic validation
        if (request.email() == null || request.password() == null) {
            return ResponseEntity.badRequest().body(error("Email und Passwort sind erforderlich"));
        }

        String email = request.email().trim().toLowerCase();

        // Find user by normalized email
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(error("Email oder Passwort falsch"));
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(error("Email oder Passwort falsch"));
        }

        // JWT-Token generieren
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(authResponse(token, user));
    }

    /**
     * Passwort-Reset fuer Benutzer die ihr Passwort vergessen haben.
     * ACHTUNG: Keine Email-Verifizierung (da kein SMTP-Server)!
     * In Produktion sollte hier ein Token per Email verschickt werden.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        if (request.email() == null || request.email().isBlank()) {
            return ResponseEntity.badRequest().body(error("Email ist erforderlich"));
        }
        if (request.newPassword() == null || request.newPassword().length() < 6) {
            return ResponseEntity.badRequest().body(error("Passwort muss mindestens 6 Zeichen haben"));
        }

        String email = request.email().trim().toLowerCase();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(error("E-Mail-Adresse nicht gefunden"));
        }

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(request.newPassword()));

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error("Fehler beim Speichern des neuen Passworts"));
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Passwort erfolgreich geändert");
        return ResponseEntity.ok(response);
    }

    /**
     * Hilfsmethode: Erstellt Error-Response.
     */
    private Map<String, String> error(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        return response;
    }

    /**
     * Hilfsmethode: Erstellt Auth-Response mit Token und User-Daten.
     */
    private Map<String, Object> authResponse(String token, User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId());
        response.put("email", user.getEmail());
        return response;
    }

    // --- Request DTOs als Records ---

    public record RegisterRequest(String email, String password) {}
    public record LoginRequest(String email, String password) {}
    public record ResetPasswordRequest(String email, String newPassword) {}
}

