package tn.esprit.autoloc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Données d'une maintenance")
public record MaintenanceRequest(
        @Schema(description = "Optionnelle : par défaut la date du jour", example = "2026-10-21") LocalDate dateDebut,
        @Schema(example = "Vidange et changement des plaquettes") @Size(max = 255) String description) {
}
