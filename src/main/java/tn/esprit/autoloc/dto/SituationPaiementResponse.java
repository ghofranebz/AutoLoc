package tn.esprit.autoloc.dto;

import java.math.BigDecimal;

public record SituationPaiementResponse(BigDecimal montantTotal, BigDecimal totalPaye, BigDecimal resteAPayer) {
}
