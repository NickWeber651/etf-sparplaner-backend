package de.htw.berlin.webtech.etf.service;

import de.htw.berlin.webtech.etf.domain.entity.Sparplan;
import de.htw.berlin.webtech.etf.domain.entity.User;
import de.htw.berlin.webtech.etf.repository.SparplanRepository;
import de.htw.berlin.webtech.etf.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SparplanService {

    private final SparplanRepository repository;
    private final UserRepository userRepository;

    /**
     * Findet alle Sparplaene eines bestimmten Users.
     * User sieht nur seine eigenen Sparplaene.
     */
    public List<Sparplan> findAllByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    /**
     * Findet einen Sparplan nur wenn er dem User gehoert.
     */
    public Optional<Sparplan> findByIdAndUserId(Long id, Long userId) {
        return repository.findByIdAndUserId(id, userId);
    }

    /**
     * Prueft ob ein Sparplan existiert (unabhaengig vom User).
     */
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    /**
     * Erstellt einen neuen Sparplan fuer den angegebenen User.
     */
    public Sparplan save(Sparplan sparplan, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User nicht gefunden"));
        sparplan.setUser(user);
        return repository.save(sparplan);
    }

    /**
     * Aktualisiert einen Sparplan nur wenn er dem User gehoert.
     * Gibt Optional.empty() zurueck wenn nicht gefunden oder nicht berechtigt.
     */
    public Optional<Sparplan> update(Long id, Sparplan updatedSparplan, Long userId) {
        return repository.findByIdAndUserId(id, userId)
                .map(existingSparplan -> {
                    existingSparplan.setEtfName(updatedSparplan.getEtfName());
                    existingSparplan.setMonatlicheRate(updatedSparplan.getMonatlicheRate());
                    existingSparplan.setLaufzeitJahre(updatedSparplan.getLaufzeitJahre());
                    return repository.save(existingSparplan);
                });
    }

    /**
     * Loescht einen Sparplan nur wenn er dem User gehoert.
     * Gibt true zurueck wenn erfolgreich, false wenn nicht gefunden.
     */
    public boolean deleteByIdAndUserId(Long id, Long userId) {
        Optional<Sparplan> sparplan = repository.findByIdAndUserId(id, userId);
        if (sparplan.isPresent()) {
            repository.delete(sparplan.get());
            return true;
        }
        return false;
    }
}

