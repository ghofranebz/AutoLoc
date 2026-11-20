package tn.esprit.autoloc.service.support;

import java.time.ZoneId;

/** Fuseau horaire unique de l'application (Clean Code : pas de LocalDate.now() sans zone). */
public final class Fuseau {

    public static final ZoneId TUNIS = ZoneId.of("Africa/Tunis");

    private Fuseau() {
        // classe utilitaire : pas d'instance
    }
}