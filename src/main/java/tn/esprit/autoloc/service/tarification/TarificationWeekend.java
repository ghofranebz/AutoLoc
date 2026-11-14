package tn.esprit.autoloc.service.tarification;

import org.springframework.stereotype.Component;

/** Tarif week-end : réduction de 10 %. */
@Component("WEEKEND")
public class TarificationWeekend implements StrategieTarification {

    private static final double REDUCTION = 0.10;

    @Override
    public double calculer(double tarifJournalier, int jours) {
        return tarifJournalier * jours * (1 - REDUCTION);
    }
}