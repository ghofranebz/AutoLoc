package tn.esprit.autoloc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Données pour créer ou modifier un équipement")
public record EquipementRequest(
        @Schema(example = "GPS") @NotBlank @Size(max = 50) String libelle) {
}
