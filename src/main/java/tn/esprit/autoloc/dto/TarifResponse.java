package tn.esprit.autoloc.dto;

import java.math.BigDecimal;

/** Résultat d'un calcul de prix de location pour un véhicule. */
public record TarifResponse(Long idVehicule, String typeTarif, int jours,
                            BigDecimal tarifJournalier, BigDecimal montant) {
}