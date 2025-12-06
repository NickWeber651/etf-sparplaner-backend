package de.htw.berlin.webtech.etf.persistence;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sparplaene")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sparplan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name darf nicht leer sein")
    @Size(max = 100)
    private String name;

    @NotNull(message = "Monatliche Rate ist erforderlich")
    @DecimalMin(value = "0.01", message = "Rate muss mindestens 0.01 sein")
    private BigDecimal monatlicheRate;

    @NotBlank(message = "ETF-Name ist erforderlich")
    @Size(max = 100)
    private String etfName;

    @NotNull(message = "Laufzeit in Monaten ist erforderlich")
    @Min(value = 1, message = "Laufzeit muss mindestens 1 Monat sein")
    private Integer laufzeitMonate;

    @NotBlank(message = "Risikoprofil ist erforderlich")
    private String risikoprofil;

    @Column(updatable = false)
    private LocalDate erstelltAm;

    @PrePersist
    protected void onCreate() {
        erstelltAm = LocalDate.now();
    }
}

