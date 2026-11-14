package tn.esprit.autoloc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Données pour créer une réservation (statut initial EN_ATTENTE)")
public record ReservationRequest(
        @Schema(example = "1") @NotNull Long idClient,
        @Schema(example = "1") @NotNull Long idVehicule,
        @Schema(example = "2026-11-01") @NotNull LocalDate dateDebut,
        @Schema(example = "2026-11-05") @NotNull LocalDate dateFin) {
}
