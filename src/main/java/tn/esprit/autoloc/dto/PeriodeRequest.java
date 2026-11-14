package tn.esprit.autoloc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Nouvelle période d'une réservation")
public record PeriodeRequest(
        @Schema(example = "2026-11-02") @NotNull LocalDate dateDebut,
        @Schema(example = "2026-11-06") @NotNull LocalDate dateFin) {
}
