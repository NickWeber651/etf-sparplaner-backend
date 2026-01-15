package de.htw.berlin.webtech.etf;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApiSmokeTest {

    @Autowired
    MockMvc mvc;

    private static String uniqueEmail() {
        return "smoketest_" + System.currentTimeMillis() + "@example.com";
    }

    @Test
    void authRegister_isPublic_andCreatesUser() throws Exception {
        // Immer eindeutige E-Mail => kein 409-Conflict => Test bleibt stabil
        String registerJson = """
                {
                    "email": "%s",
                    "password": "password123"
                }
                """.formatted(uniqueEmail());

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson)
                        .header("Origin", "http://localhost:5173"))
                .andExpect(status().isCreated());
    }

    @Test
    void protectedEndpoints_requireAuth() throws Exception {
        mvc.perform(get("/api/sparplaene"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cors_preflight_allowsViteOrigin_forAuthEndpoints() throws Exception {
        // Preflight ist der sauberste CORS-Test: unabhängig davon ob Login-User existiert
        mvc.perform(options("/api/auth/login")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Controlå‚-Request-Method", "POST")
                        .header("Access-Controlå-Request-Headers", "Content-Type, Authorization"))
                // je nach Setup ist 200 oder 204 üblich; Spring liefert oft 200
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().string("Vary", org.hamcrest.Matchers.containsString("Origin")))
                .andExpect(header().string("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString("POST")));
    }
}