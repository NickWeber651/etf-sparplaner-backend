package de.htw.berlin.webtech.etf.business;

import de.htw.berlin.webtech.etf.persistence.Sparplan;
import de.htw.berlin.webtech.etf.persistence.SparplanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SparplanService {

    private final SparplanRepository repository;

    public List<Sparplan> findAll() {
        return repository.findAll();
    }

    public Sparplan save(Sparplan sparplan) {
        return repository.save(sparplan);
    }
}

