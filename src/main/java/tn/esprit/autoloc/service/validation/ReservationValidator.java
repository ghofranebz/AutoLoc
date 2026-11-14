package tn.esprit.autoloc.service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.exception.ConflictException;
import tn.esprit.autoloc.repository.ReservationRepository;
import tn.esprit.autoloc.service.support.PeriodeValidator;

import java.time.LocalDate;

/**
 * SRP (cours, slide « ProjetValidator ») : les règles de validation d'une réservation sont sorties de
 * ReservationServiceImpl. Le service orchestre (charger, valider, enregistrer, notifier) ; cette classe
 * ne fait que valider. Elle change uniquement si une règle métier de réservation change.
 */
@Component
@RequiredArgsConstructor
public class ReservationValidator {

    private final ReservationRepository reservationRepository;
    private final PeriodeValidator periodeValidator;

    /** Règles à la création : période cohérente, véhicule réservable, pas de chevauchement. */
    public void validerCreation(Vehicule vehicule, LocalDate debut, LocalDate fin) {
        periodeValidator.verifier(debut, fin);
        verifierVehiculeReservable(vehicule);
        if (reservationRepository.existsChevauchement(vehicule.getIdVehicule(), debut, fin)) {
            throw new ConflictException("Le véhicule " + vehicule.getIdVehicule() + " est déjà réservé sur cette période");
        }
    }

    /** Règles au changement de dates : période cohérente et pas de chevauchement avec une AUTRE réservation active. */
    public void validerNouvellesDates(Long idReservation, Vehicule vehicule, LocalDate debut, LocalDate fin) {
        periodeValidator.verifier(debut, fin);
        boolean conflit = reservationRepository.findByVehicule_IdVehicule(vehicule.getIdVehicule())
                .stream()
                .filter(r -> !r.getIdReservation().equals(idReservation))
                .filter(r -> r.getStatut() != StatutReservation.ANNULEE)
                .anyMatch(r -> !r.getDateDebut().isAfter(fin) && !r.getDateFin().isBefore(debut));
        if (conflit) {
            throw new ConflictException("Le véhicule est déjà réservé sur cette nouvelle période");
        }
    }

    private void verifierVehiculeReservable(Vehicule vehicule) {
        if (vehicule.getStatut() == StatutVehicule.MAINTENANCE) {
            throw new BusinessRuleException("Le véhicule " + vehicule.getIdVehicule() + " est en maintenance");
        }
    }
}
