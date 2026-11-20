package tn.esprit.autoloc.service.tarification;

import org.springframework.stereotype.Component;

/**
 * Atelier GoF/SOLID, partie C, étape 4 : politique « entreprise », réduction de 15 %.
 * Ajoutée sans modifier TarificationService ni aucune autre classe (principe OCP).
 */
@Component("entreprise")
public class TarificationEntreprise implements StrategieTarification {

    private static final double REDUCTION = 0.15;

    @Override
    public double calculer(double tarifJournalier, int jours) {
        return tarifJournalier * jours * (1 - REDUCTION);
    }
}