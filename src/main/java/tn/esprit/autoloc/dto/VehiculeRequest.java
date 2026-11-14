package tn.esprit.autoloc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;

import java.math.BigDecimal;

@Schema(description = "Données pour créer ou modifier un véhicule")
public record VehiculeRequest(
        @Schema(example = "123 TUN 4567") @NotBlank @Size(max = 20) String immatriculation,
        @Schema(example = "Peugeot") @NotBlank @Size(max = 50) String marque,
        @Schema(example = "208") @NotBlank @Size(max = 50) String modele,
        @NotNull CategorieVehicule categorie,
        @Schema(description = "Tarif par jour, strictement positif", example = "90.00")
        @NotNull @Positive @Digits(integer = 8, fraction = 2) BigDecimal tarifJournalier,
        @Schema(description = "Optionnel : DISPONIBLE par défaut à la création") StatutVehicule statut,
        @Schema(description = "Agence propriétaire (création uniquement ; ensuite utiliser l'affectation).",
                example = "1") Long idAgence) {
}
