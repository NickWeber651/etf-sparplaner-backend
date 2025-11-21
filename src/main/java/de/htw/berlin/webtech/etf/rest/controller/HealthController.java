package de.htw.berlin.webtech.etf.rest.controller;

import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;

/**
 * Health-Endpoint:
 * Liefert ein kleines JSON, mit dem Frontend/Monitoring prüfen kann,
 * ob der Dienst "lebt" (status=UP) – ohne Fachlogik zu berühren.
 */
@RestController                       // macht die Klasse zu einem Web-Controller (JSON)
@RequestMapping("/api")               // alle Methoden hier hängen unter /api
@CrossOrigin(origins = "http://localhost:5173")
public class HealthController {
    /**
     * GET /api/health
     * Gibt { "status": "UP", "service": "...", "timestamp": "..." } zurück.
     * - status: UP/down (hier immer UP, weil Server antwortet)
     * - service: Klartext-Name deines Backends
     * - timestamp: Zeitpunkt der Antwort (ISO-String)
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "etf-sparplaner-backend",
                "timestamp", Instant.now().toString()
        );
    }
}
