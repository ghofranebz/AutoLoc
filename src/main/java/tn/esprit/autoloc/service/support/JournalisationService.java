package tn.esprit.autoloc.service.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Atelier GoF/SOLID, partie B.1 (SRP) : journalisation centralisée (remplace System.out.println). */
@Service
public class JournalisationService {

    private static final Logger log = LoggerFactory.getLogger(JournalisationService.class);

    public void operation(String action, String entite, Object identifiant) {
        log.info("[AutoLoc] {} du {} {}", action, entite, identifiant);
    }
}