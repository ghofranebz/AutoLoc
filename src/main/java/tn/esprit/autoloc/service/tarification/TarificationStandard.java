package tn.esprit.autoloc.service.tarification;

import org.springframework.stereotype.Component;

/** Tarif standard : tarif journalier x nombre de jours. */
@Component("STANDARD")
public class TarificationStandard implements StrategieTarification {

    @Override
    public double calculer(double tarifJournalier, int jours) {
        return tarifJournalier * jours;
    }
}