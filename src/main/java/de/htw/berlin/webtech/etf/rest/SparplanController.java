package de.htw.berlin.webtech.etf.rest;

import de.htw.berlin.webtech.etf.business.SparplanService;
import de.htw.berlin.webtech.etf.persistence.Sparplan;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sparplaene")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SparplanController {

    private final SparplanService service;

    @GetMapping
    public List<Sparplan> getAllSparplaene() {
        return service.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Sparplan createSparplan(@Valid @RequestBody Sparplan sparplan) {
        return service.save(sparplan);
    }
}

