package de.htw.berlin.webtech.etf.repository;

import de.htw.berlin.webtech.etf.domain.entity.Sparplan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SparplanRepository extends JpaRepository<Sparplan, Long> {

    /**
     * Findet alle Sparplaene eines Users.
     * Wird verwendet um nur die eigenen Sparplaene anzuzeigen.
     */
    List<Sparplan> findByUserId(Long userId);

    /**
     * Findet einen Sparplan nur wenn er dem User gehoert.
     * Verhindert Zugriff auf fremde Sparplaene.
     */
    Optional<Sparplan> findByIdAndUserId(Long id, Long userId);
}

