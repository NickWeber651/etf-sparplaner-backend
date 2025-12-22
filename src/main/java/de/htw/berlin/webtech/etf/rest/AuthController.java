package de.htw.berlin.webtech.etf.rest;

import de.htw.berlin.webtech.etf.business.JwtService;
import de.htw.berlin.webtech.etf.persistence.User;
import de.htw.berlin.webtech.etf.persistence.UserRepository;
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
        // Validierung
        if (request.email() == null || request.email().isBlank()) {
            return ResponseEntity.badRequest().body(error("Email ist erforderlich"));
        }
        if (request.password() == null || request.password().length() < 6) {
            return ResponseEntity.badRequest().body(error("Passwort muss mindestens 6 Zeichen haben"));
        }

        // Pruefen ob Email bereits existiert (keine Duplikate!)
        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(error("Email ist bereits registriert"));
        }

        // Neuen User erstellen
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);

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
        // Validierung
        if (request.email() == null || request.password() == null) {
            return ResponseEntity.badRequest().body(error("Email und Passwort sind erforderlich"));
        }

        // User suchen
        Optional<User> userOptional = userRepository.findByEmail(request.email());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(error("Email oder Passwort falsch"));
        }

        User user = userOptional.get();

        // Passwort pruefen
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(error("Email oder Passwort falsch"));
        }

        // JWT-Token generieren
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(authResponse(token, user));
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
}

