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

    @NotBlank(message = "ETF-Name ist erforderlich")
    @Size(max = 200)
    private String etfName;

    @NotNull(message = "Monatliche Rate ist erforderlich")
    @DecimalMin(value = "0.01", message = "Rate muss mindestens 0.01 sein")
    private BigDecimal monatlicheRate;

    @NotNull(message = "Laufzeit ist erforderlich")
    @Min(value = 1, message = "Laufzeit muss mindestens 1 Jahr sein")
    private Integer laufzeitJahre;

    @Column(updatable = false)
    private LocalDate erstelltAm;

    @PrePersist
    protected void onCreate() {
        erstelltAm = LocalDate.now();
    }
}

