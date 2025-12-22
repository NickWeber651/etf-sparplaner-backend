package de.htw.berlin.webtech.etf.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository für User-Datenbankoperationen.
 *
 * Spring Data JPA generiert die SQL-Queries automatisch basierend auf den Methodennamen!
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Findet einen User anhand seiner Email.
     * Wird beim LOGIN verwendet, um den User zu authentifizieren.
     *
     * @param email Die Email-Adresse
     * @return Optional<User> - leer wenn nicht gefunden
     */
    Optional<User> findByEmail(String email);

    /**
     * Prüft ob eine Email bereits registriert ist.
     * Wird bei der REGISTRIERUNG verwendet, um Duplikate zu verhindern.
     *
     * @param email Die Email-Adresse
     * @return true wenn Email bereits existiert
     */
    boolean existsByEmail(String email);
}

