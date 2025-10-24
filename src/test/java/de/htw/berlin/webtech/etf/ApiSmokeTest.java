package de.htw.berlin.webtech.etf;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Smoke-Tests (Schnelltests) fürs Backend.
 *
 * Ziel:
 * - Nachweisen, dass die wichtigsten Endpunkte erreichbar sind (Lebt der Service? Liefert /etfs Daten?)
 * - Nachweisen, dass CORS für das Frontend (Vite: http://localhost:5173) korrekt konfiguriert ist.
 *
 * Warum wichtig für M2?
 * - Das Vue-Frontend darf im Browser nur dann auf das Backend zugreifen,
 *   wenn der Server die passenden CORS-Header zurückgibt.
 * - /api/health dient als minimaler "Lebt der Dienst?"-Check (Badge im FE, CI, Deployment-Checks).
 */
@SpringBootTest
@AutoConfigureMockMvc
class ApiSmokeTest {

    @Autowired
    MockMvc mvc;

    @Test
    void etfs_returnsOk_and_hasCorsForVite() throws Exception {
        // Arrange & Act:
        // Sende eine GET-Anfrage auf /etfs und simuliere dabei den Browser-Origin des Vite-Dev-Servers.
        // -> Der Origin-Header ist entscheidend, damit der Server CORS-Header setzt.
        mvc.perform(
                        get("/etfs")
                                .header("Origin", "http://localhost:5173")
                )
                // Assert 1: HTTP 200 = Endpunkt ist erreichbar und liefert erfolgreich JSON.
                .andExpect(status().isOk())
                // Assert 2: CORS-Header ist korrekt gesetzt → Browser lässt die Antwort passieren.
                // Ohne diesen Header würde das Frontend eine CORS-Fehlermeldung bekommen.
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    void health_isUp() throws Exception {
        // Arrange & Act:
        // Rufe den Health-Endpoint auf. Er soll ein kleines JSON liefern, das "UP" signalisiert.
        mvc.perform(get("/api/health"))
                // Assert 1: Endpunkt reagiert (200 OK) → Serverprozess läuft und ist per HTTP erreichbar.
                .andExpect(status().isOk())
                // Assert 2: Inhaltlich erwartet: status=UP → wird im Frontend/Monitoring als "grün" angezeigt.
                .andExpect(jsonPath("$.status").value("UP"));

    }
}

