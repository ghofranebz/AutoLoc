package tn.esprit.autoloc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.TarifResponse;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.repository.VehiculeRepository;
import tn.esprit.autoloc.service.ITarificationService;
import tn.esprit.autoloc.service.support.EntityLoader;
import tn.esprit.autoloc.service.tarification.StrategieTarification;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Atelier GoF/SOLID, partie C, étape 3 : utilise les stratégies de tarification.
 * Spring injecte une Map contenant TOUS les beans StrategieTarification, indexés par leur nom
 * (STANDARD, WEEKEND, LONGUE_DUREE, ENTREPRISE). Plus aucun if/else sur le type (Strategy + OCP).
 */
@Service
@RequiredArgsConstructor
public class TarificationService implements ITarificationService {

    private final Map<String, StrategieTarification> strategies;
    private final VehiculeRepository vehiculeRepository;
    private final EntityLoader loader;

    @Override
    public double calculer(String typeTarif, double tarifJournalier, int jours) {
        if (jours <= 0) {
            throw new BusinessRuleException("Le nombre de jours doit être positif");
        }
        if (tarifJournalier <= 0) {
            throw new BusinessRuleException("Le tarif doit être positif");
        }
        double prix = strategie(typeTarif).calculer(tarifJournalier, jours);
        return BigDecimal.valueOf(prix).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    @Transactional(readOnly = true)
    public TarifResponse calculerPourVehicule(Long idVehicule, String typeTarif, int jours) {
        Vehicule vehicule = loader.charger(vehiculeRepository, idVehicule, "Vehicule");
        double prix = calculer(typeTarif, vehicule.getTarifJournalier().doubleValue(), jours);
        return new TarifResponse(idVehicule, normaliser(typeTarif), jours, vehicule.getTarifJournalier(),
                BigDecimal.valueOf(prix).setScale(2, RoundingMode.HALF_UP));
    }

    @Override
    public Set<String> typesDisponibles() {
        return new TreeSet<>(strategies.keySet());
    }

    @Override
    public boolean existe(String typeTarif) {
        return strategies.containsKey(normaliser(typeTarif));
    }

    /** Question 4 : type inconnu -> erreur 400 claire avec la liste des types (pas de formule par défaut). */
    private StrategieTarification strategie(String typeTarif) {
        StrategieTarification strategie = strategies.get(normaliser(typeTarif));
        if (strategie == null) {
            throw new BusinessRuleException("Type de tarif inconnu : '" + typeTarif
                    + "'. Types disponibles : " + typesDisponibles());
        }
        return strategie;
    }

    /** "longue-duree", "Longue_Duree" et "LONGUE_DUREE" désignent la même stratégie. */
    private String normaliser(String typeTarif) {
        return typeTarif == null ? "" : typeTarif.trim().toUpperCase().replace('-', '_');
    }
}