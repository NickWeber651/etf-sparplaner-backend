package de.htw.berlin.webtech.etf.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

/**
 * Integrationstest fuer SecurityFilterChain Methode
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityFilterChainIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test fuer securityFilterChain - Oeffentliche Endpoints sollen erreichbar sein
     */
    @Test
    void securityFilterChain_PublicEndpoints_ShouldBeAccessibleWithoutAuthentication() throws Exception {
        // Auth-Endpoints sollten ohne Token erreichbar sein (405 oder 404 ist OK, wichtig ist NICHT 401)
        mockMvc.perform(get("/api/auth/test"))
            .andExpect(result -> {
                int status = result.getResponse().getStatus();
                assertThat(status).isNotEqualTo(401); // 401 = Unauthorized waere falsch
            });
    }

    /**
     * Test fuer securityFilterChain - Geschuetzte Endpoints erfordern Authentifizierung
     */
    @Test
    void securityFilterChain_ProtectedEndpoints_ShouldReturn401WithoutAuthentication() throws Exception {
        // Ein nicht-oeffentlicher Endpoint sollte 401 zurueckgeben
        mockMvc.perform(get("/api/sparplaene"))
            .andExpect(status().isUnauthorized());
    }

    /**
     * Test fuer securityFilterChain - CORS Headers bei OPTIONS Request
     */
    @Test
    void securityFilterChain_CorsEnabled_ShouldHandleOptionsRequest() throws Exception {
        mockMvc.perform(options("/api/auth/login")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "POST"))
            .andExpect(result -> {
                // OPTIONS sollte nicht zu 401 fuehren
                int status = result.getResponse().getStatus();
                assertThat(status).isIn(200, 204, 403); // Verschiedene Status moeglich, aber nicht 401
            });
    }

    /**
     * Test fuer securityFilterChain - H2 Console sollte erreichbar sein (development)
     */
    @Test
    void securityFilterChain_H2Console_ShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/h2-console/"))
            .andExpect(result -> {
                int status = result.getResponse().getStatus();
                // Sollte nicht 401 sein (koennte 404 oder 200 sein je nach Konfiguration)
                assertThat(status).isNotEqualTo(401);
            });
    }

    private static org.assertj.core.api.AbstractIntegerAssert<?> assertThat(int actual) {
        return org.assertj.core.api.Assertions.assertThat(actual);
    }
}

