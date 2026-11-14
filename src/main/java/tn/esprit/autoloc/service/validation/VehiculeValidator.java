package tn.esprit.autoloc.service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.VehiculeRequest;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.exception.ConflictException;
import tn.esprit.autoloc.repository.MaintenanceRepository;
import tn.esprit.autoloc.repository.ReservationRepository;
import tn.esprit.autoloc.repository.VehiculeRepository;

import java.math.BigDecimal;

/** Atelier GoF/SOLID, partie B.1 (SRP) : règles de validation métier d'un véhicule. */
@Component
@RequiredArgsConstructor
public class VehiculeValidator {

    private final VehiculeRepository vehiculeRepository;
    private final ReservationRepository reservationRepository;
    private final MaintenanceRepository maintenanceRepository;

    public void validerAjout(VehiculeRequest request) {
        verifierTarif(request.tarifJournalier());
        if (vehiculeRepository.existsByImmatriculation(request.immatriculation())) {
            throw new ConflictException("Le véhicule " + request.immatriculation() + " existe déjà");
        }
    }

    public void validerModification(Vehicule existant, VehiculeRequest request) {
        verifierTarif(request.tarifJournalier());
        if (!request.immatriculation().equals(existant.getImmatriculation())
                && vehiculeRepository.existsByImmatriculation(request.immatriculation())) {
            throw new ConflictException("L'immatriculation " + request.immatriculation() + " est déjà utilisée");
        }
    }

    public void validerSuppression(Long idVehicule) {
        if (!reservationRepository.findByVehicule_IdVehicule(idVehicule).isEmpty()
                || !maintenanceRepository.findByVehicule_IdVehicule(idVehicule).isEmpty()) {
            throw new BusinessRuleException("Impossible de supprimer le véhicule " + idVehicule
                    + " : il a des réservations ou des maintenances");
        }
    }

    private void verifierTarif(BigDecimal tarifJournalier) {
        if (tarifJournalier == null || tarifJournalier.signum() <= 0) {
            throw new BusinessRuleException("Le tarif doit être positif");
        }
    }
}