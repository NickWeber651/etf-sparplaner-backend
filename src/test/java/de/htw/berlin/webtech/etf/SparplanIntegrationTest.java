package de.htw.berlin.webtech.etf;

import de.htw.berlin.webtech.etf.persistence.Sparplan;
import de.htw.berlin.webtech.etf.rest.AuthController.LoginRequest;
import de.htw.berlin.webtech.etf.rest.AuthController.RegisterRequest;
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

    @BeforeEach
    void setUp() {
        // Registriere einen Test-User und hole JWT-Token
        RegisterRequest registerRequest = new RegisterRequest(
                "test" + System.currentTimeMillis() + "@example.com",
                "password123"
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/auth/register",
                registerRequest,
                Map.class
        );

        if (response.getStatusCode() == HttpStatus.CREATED) {
            jwtToken = (String) response.getBody().get("token");
        }
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);
        return headers;
    }

    @Test
    void shouldCreateAndRetrieveSparplan() {
        // Create a new Sparplan
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
        assertThat(postResponse.getBody().getEtfName()).isEqualTo("S&P 500 (TER: 0.07 %)");
        assertThat(postResponse.getBody().getMonatlicheRate()).isEqualByComparingTo(new BigDecimal("200.00"));
        assertThat(postResponse.getBody().getLaufzeitJahre()).isEqualTo(15);
        assertThat(postResponse.getBody().getErstelltAm()).isNotNull();

        // Retrieve all Sparplaene
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
    void shouldReturnEmptyListForNewUser() {
        // Registriere einen neuen User
        RegisterRequest registerRequest = new RegisterRequest(
                "newuser" + System.currentTimeMillis() + "@example.com",
                "password123"
        );

        ResponseEntity<Map> authResponse = restTemplate.postForEntity(
                "/api/auth/register",
                registerRequest,
                Map.class
        );

        String newUserToken = (String) authResponse.getBody().get("token");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(newUserToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Sparplan[]> response = restTemplate.exchange(
                "/api/sparplaene",
                HttpMethod.GET,
                request,
                Sparplan[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
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

