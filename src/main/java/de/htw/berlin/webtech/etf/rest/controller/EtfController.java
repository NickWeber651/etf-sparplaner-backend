package de.htw.berlin.webtech.etf.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EtfController {

    @GetMapping("/")
    public String home() {
        return "ETF-Sparplaner Backend läuft ✅  → Daten unter /etfs";
    }

    record Etf(long id, String name, String isin, double ter) {}

    @GetMapping("/etfs")
    public List<Etf> listEtfs() {
        return List.of(
                new Etf(1, "iShares Core MSCI World", "IE00B4L5Y983", 0.20),
                new Etf(2, "Vanguard FTSE All-World", "IE00B3RBWM25", 0.22),
                new Etf(3, "Xtrackers MSCI EM IMI", "IE00BTJRMP35", 0.18)
        );
    }
}
//Test