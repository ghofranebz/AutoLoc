package tn.esprit.autoloc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Données pour créer ou modifier une agence")
public record AgenceRequest(
        @Schema(example = "AutoLoc Tunis Centre") @NotBlank @Size(max = 100) String nom,
        @Schema(example = "Tunis") @NotBlank @Size(max = 50) String ville,
        @Schema(example = "12 avenue Habib Bourguiba") @NotBlank @Size(max = 150) String adresse,
        @Schema(example = "71000000") @Size(max = 20) String telephone) {
}
