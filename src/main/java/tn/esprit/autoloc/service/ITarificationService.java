package tn.esprit.autoloc.service;

import tn.esprit.autoloc.dto.TarifResponse;

import java.util.Set;

/** Contrat du calcul des prix de location (DIP / ISP). */
public interface ITarificationService {

    double calculer(String typeTarif, double tarifJournalier, int jours);

    TarifResponse calculerPourVehicule(Long idVehicule, String typeTarif, int jours);

    Set<String> typesDisponibles();

    boolean existe(String typeTarif);
}