package tn.esprit.autoloc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import tn.esprit.autoloc.domain.ModePaiement;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Données d'un paiement sur un contrat")
public record PaiementRequest(
        @Schema(description = "Strictement positif, ne peut pas dépasser le reste à payer", example = "100.00")
        @NotNull @Positive @Digits(integer = 8, fraction = 2) BigDecimal montant,
        @Schema(description = "Optionnelle : par défaut la date du jour", example = "2026-10-21") LocalDate datePaiement,
        @NotNull ModePaiement modePaiement) {
}
