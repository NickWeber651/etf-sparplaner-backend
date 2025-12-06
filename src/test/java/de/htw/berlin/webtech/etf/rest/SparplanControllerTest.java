package de.htw.berlin.webtech.etf.rest;

import de.htw.berlin.webtech.etf.business.SparplanService;
import de.htw.berlin.webtech.etf.persistence.Sparplan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SparplanController.class)
class SparplanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SparplanService service;

    @Test
    void shouldReturnAllSparplaene() throws Exception {
        Sparplan sparplan = new Sparplan();
        sparplan.setId(1L);
        sparplan.setEtfName("S&P 500 (TER: 0.07 %)");
        sparplan.setMonatlicheRate(new BigDecimal("200.00"));
        sparplan.setLaufzeitJahre(15);

        List<Sparplan> sparplaene = Arrays.asList(sparplan);
        when(service.findAll()).thenReturn(sparplaene);

        mockMvc.perform(get("/api/sparplaene"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].etfName").value("S&P 500 (TER: 0.07 %)"))
                .andExpect(jsonPath("$[0].monatlicheRate").value(200.00))
                .andExpect(jsonPath("$[0].laufzeitJahre").value(15));
    }

    @Test
    void shouldCreateSparplan() throws Exception {
        Sparplan sparplan = new Sparplan();
        sparplan.setId(1L);
        sparplan.setEtfName("Vanguard FTSE All-World (TER: 0.22 %)");
        sparplan.setMonatlicheRate(new BigDecimal("150.00"));
        sparplan.setLaufzeitJahre(20);

        when(service.save(any(Sparplan.class))).thenReturn(sparplan);

        String json = """
                {
                    "etfName": "Vanguard FTSE All-World (TER: 0.22 %)",
                    "monatlicheRate": 150.00,
                    "laufzeitJahre": 20
                }
                """;

        mockMvc.perform(post("/api/sparplaene")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.etfName").value("Vanguard FTSE All-World (TER: 0.22 %)"))
                .andExpect(jsonPath("$.monatlicheRate").value(150.00))
                .andExpect(jsonPath("$.laufzeitJahre").value(20));
    }
}

