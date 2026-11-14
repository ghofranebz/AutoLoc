package tn.esprit.autoloc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tn.esprit.autoloc.domain.RoleEmploye;

@Schema(description = "Données pour créer ou modifier un employé")
public record EmployeRequest(
        @Schema(example = "Trabelsi") @NotBlank @Size(max = 50) String nom,
        @Schema(example = "Sami") @NotBlank @Size(max = 50) String prenom,
        @NotNull RoleEmploye role,
        @Schema(description = "Agence de rattachement (création uniquement ; ensuite utiliser l'affectation).",
                example = "1") Long idAgence) {
}
