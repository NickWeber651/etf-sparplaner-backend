package de.htw.berlin.webtech.etf;

import de.htw.berlin.webtech.etf.persistence.Sparplan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SparplanIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateAndRetrieveSparplan() {
        // Create a new Sparplan
        Sparplan sparplan = new Sparplan();
        sparplan.setEtfName("S&P 500 (TER: 0.07 %)");
        sparplan.setMonatlicheRate(new BigDecimal("200.00"));
        sparplan.setLaufzeitJahre(15);

        ResponseEntity<Sparplan> postResponse = restTemplate.postForEntity(
                "/api/sparplaene",
                sparplan,
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
        ResponseEntity<Sparplan[]> getResponse = restTemplate.getForEntity(
                "/api/sparplaene",
                Sparplan[].class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().length).isGreaterThan(0);
    }

    @Test
    void shouldReturnEmptyListWhenNoSparplaene() {
        ResponseEntity<Sparplan[]> response = restTemplate.getForEntity(
                "/api/sparplaene",
                Sparplan[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}

