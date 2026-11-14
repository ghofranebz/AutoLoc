package tn.esprit.autoloc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Données pour créer ou modifier un client")
public record ClientRequest(
        @Schema(example = "Ben Salah") @NotBlank @Size(max = 50) String nom,
        @Schema(example = "Ahmed") @NotBlank @Size(max = 50) String prenom,
        @Schema(example = "ahmed.bensalah@example.com") @NotBlank @Email @Size(max = 100) String email,
        @Schema(example = "98123456") @Size(max = 20) String telephone,
        @Schema(example = "PERMIS-123456") @NotBlank @Size(max = 30) String numPermis,
        @Schema(description = "Optionnelle : par défaut la date du jour. Ignorée lors d'une modification.",
                example = "2026-10-21") LocalDate dateInscription) {
}
