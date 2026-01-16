package de.htw.berlin.webtech.etf;

import de.htw.berlin.webtech.etf.domain.entity.Sparplan;
import de.htw.berlin.webtech.etf.controller.AuthController.RegisterRequest;
import de.htw.berlin.webtech.etf.controller.AuthController.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SparplanIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String jwtToken;
    private String email;
    private final String password = "password123";

    @BeforeEach
    void setUp() {
        email = "test" + System.currentTimeMillis() + "@example.com";

        // 1) Register
        RegisterRequest registerRequest = new RegisterRequest(email, password);
        ResponseEntity<Map> registerResponse = restTemplate.postForEntity(
                "/api/auth/register",
                registerRequest,
                Map.class
        );

        if (registerResponse.getStatusCode() == HttpStatus.CREATED && registerResponse.getBody() != null) {
            jwtToken = (String) registerResponse.getBody().get("token");
        }

        // 2) Falls kein Token (oder Register-Flow anders): Login versuchen
        if (jwtToken == null) {
            LoginRequest loginRequest = new LoginRequest(email, password);
            ResponseEntity<Map> loginResponse = restTemplate.postForEntity(
                    "/api/auth/login",
                    loginRequest,
                    Map.class
            );

            if (loginResponse.getStatusCode().is2xxSuccessful() && loginResponse.getBody() != null) {
                jwtToken = (String) loginResponse.getBody().get("token");
            }
        }

        assertThat(jwtToken)
                .as("JWT Token must be available for integration tests (register or login should return it)")
                .isNotNull();
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);
        return headers;
    }

    @Test
    void shouldCreateAndRetrieveSparplan() {
        Sparplan sparplan = new Sparplan();
        sparplan.setEtfName("S&P 500 (TER: 0.07 %)");
        sparplan.setMonatlicheRate(new BigDecimal("200.00"));
        sparplan.setLaufzeitJahre(15);

        HttpEntity<Sparplan> request = new HttpEntity<>(sparplan, createAuthHeaders());

        ResponseEntity<Sparplan> postResponse = restTemplate.exchange(
                "/api/sparplaene",
                HttpMethod.POST,
                request,
                Sparplan.class
        );

        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(postResponse.getBody()).isNotNull();
        assertThat(postResponse.getBody().getId()).isNotNull();

        HttpEntity<Void> getRequest = new HttpEntity<>(createAuthHeaders());

        ResponseEntity<Sparplan[]> getResponse = restTemplate.exchange(
                "/api/sparplaene",
                HttpMethod.GET,
                getRequest,
                Sparplan[].class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().length).isGreaterThan(0);
    }

    @Test
    void shouldReturn401WithoutAuthentication() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/sparplaene",
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}