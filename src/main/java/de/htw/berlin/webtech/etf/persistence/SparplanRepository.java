package de.htw.berlin.webtech.etf.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SparplanRepository extends JpaRepository<Sparplan, Long> {
}

