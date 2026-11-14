package tn.esprit.autoloc.service.support;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.exception.BusinessRuleException;

import java.time.LocalDate;

/** SRP : validation d'une période [debut, fin], partagée par les services de réservation et de véhicule. */
@Component
public class PeriodeValidator {

    public void verifier(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null) {
            throw new BusinessRuleException("dateDebut et dateFin sont obligatoires");
        }
        if (fin.isBefore(debut)) {
            throw new BusinessRuleException("La date de fin doit être postérieure ou égale à la date de début");
        }
    }
}
