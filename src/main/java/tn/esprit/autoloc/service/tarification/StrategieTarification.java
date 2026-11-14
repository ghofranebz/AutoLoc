package tn.esprit.autoloc.service.tarification;

/**
 * Atelier GoF/SOLID, partie C : abstraction du patron Strategy.
 * Chaque politique de prix = une classe @Component("NOM") qui implémente cette interface (OCP).
 */
public interface StrategieTarification {

    double calculer(double tarifJournalier, int jours);
}