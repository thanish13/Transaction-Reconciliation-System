package org.t13.app.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Settlement {

    private String settlementId;
    private String lifecycleId;
    private LocalDate settlementDate;
    private BigDecimal settlementAmount;
    private String settlementType; // "DEBIT" or "CREDIT"
    private String currency;
    private LocalDateTime processedAt;

}
