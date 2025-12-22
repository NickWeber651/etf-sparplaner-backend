package de.htw.berlin.webtech.etf.rest;

import de.htw.berlin.webtech.etf.business.SparplanService;
import de.htw.berlin.webtech.etf.persistence.Sparplan;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller fuer Sparplan-Operationen.
 * Alle Endpoints sind geschuetzt - User sieht nur seine eigenen Sparplaene.
 */
@RestController
@RequestMapping("/api/sparplaene")
@RequiredArgsConstructor
public class SparplanController {

    private final SparplanService service;

    /**
     * Holt alle Sparplaene des eingeloggten Users.
     */
    @GetMapping
    public List<Sparplan> getAllSparplaene(Authentication authentication) {
        Long userId = getUserId(authentication);
        return service.findAllByUserId(userId);
    }

    /**
     * Holt einen einzelnen Sparplan (nur wenn er dem User gehoert).
     * 404 wenn nicht gefunden, 403 wenn fremder User.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Sparplan> getSparplan(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserId(authentication);

        // Erst pruefen ob der Sparplan dem User gehoert
        var sparplan = service.findByIdAndUserId(id, userId);
        if (sparplan.isPresent()) {
            return ResponseEntity.ok(sparplan.get());
        }

        // Sparplan existiert, gehoert aber anderem User -> 403
        if (service.existsById(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Sparplan existiert nicht -> 404
        return ResponseEntity.notFound().build();
    }

    /**
     * Erstellt einen neuen Sparplan fuer den eingeloggten User.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Sparplan createSparplan(@Valid @RequestBody Sparplan sparplan, Authentication authentication) {
        Long userId = getUserId(authentication);
        return service.save(sparplan, userId);
    }

    /**
     * Aktualisiert einen bestehenden Sparplan (nur wenn er dem User gehoert).
     * 404 wenn nicht gefunden, 403 wenn fremder User.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Sparplan> updateSparplan(
            @PathVariable Long id,
            @Valid @RequestBody Sparplan sparplan,
            Authentication authentication) {
        Long userId = getUserId(authentication);

        var updated = service.update(id, sparplan, userId);
        if (updated.isPresent()) {
            return ResponseEntity.ok(updated.get());
        }

        // Sparplan existiert, gehoert aber anderem User -> 403
        if (service.existsById(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Sparplan existiert nicht -> 404
        return ResponseEntity.notFound().build();
    }

    /**
     * Loescht einen Sparplan (nur wenn er dem User gehoert).
     * 404 wenn nicht gefunden, 403 wenn fremder User.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSparplan(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserId(authentication);

        if (service.deleteByIdAndUserId(id, userId)) {
            return ResponseEntity.noContent().build();
        }

        // Sparplan existiert, gehoert aber anderem User -> 403
        if (service.existsById(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Sparplan existiert nicht -> 404
        return ResponseEntity.notFound().build();
    }

    /**
     * Hilfsmethode: Extrahiert User-ID aus Authentication-Objekt.
     * Die User-ID wurde vom JwtAuthenticationFilter in den SecurityContext gesetzt.
     */
    private Long getUserId(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }
}

