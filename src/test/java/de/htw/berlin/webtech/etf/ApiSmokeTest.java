package de.htw.berlin.webtech.etf;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Smoke-Tests (Schnelltests) fürs Backend.
 *
 * Ziel:
 * - Nachweisen, dass die wichtigsten Endpunkte erreichbar sind
 * - Nachweisen, dass CORS für das Frontend (Vite: http://localhost:5173) korrekt konfiguriert ist.
 * - Nachweisen, dass Auth-Endpoints funktionieren
 */
@SpringBootTest
@AutoConfigureMockMvc
class ApiSmokeTest {

    @Autowired
    MockMvc mvc;

    @Test
    void authEndpoints_arePublic() throws Exception {
        // Auth-Endpoints muessen ohne Authentifizierung erreichbar sein
        String registerJson = """
                {
                    "email": "smoketest@example.com",
                    "password": "password123"
                }
                """;

        // Register-Endpoint ist erreichbar (entweder Created oder Conflict wenn Email existiert)
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson)
                        .header("Origin", "http://localhost:5173"))
                .andExpect(status().isCreated());
    }

    @Test
    void protectedEndpoints_requireAuth() throws Exception {
        // Geschuetzte Endpoints sollten 401 zurueckgeben ohne Token
        mvc.perform(get("/api/sparplaene"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cors_isConfiguredForVite() throws Exception {
        // CORS-Header sollte bei Auth-Endpoints gesetzt sein
        String loginJson = """
                {
                    "email": "test@example.com",
                    "password": "password123"
                }
                """;

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                        .header("Origin", "http://localhost:5173"))
                // CORS-Header ist korrekt gesetzt
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }
}

