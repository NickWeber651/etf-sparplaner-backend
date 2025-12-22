package de.htw.berlin.webtech.etf.business;

import de.htw.berlin.webtech.etf.persistence.Sparplan;
import de.htw.berlin.webtech.etf.persistence.SparplanRepository;
import de.htw.berlin.webtech.etf.persistence.User;
import de.htw.berlin.webtech.etf.persistence.UserRepository;
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
     * Erstellt einen neuen Sparplan fuer den angegebenen User.
     */
    public Sparplan save(Sparplan sparplan, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User nicht gefunden"));
        sparplan.setUser(user);
        return repository.save(sparplan);
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

