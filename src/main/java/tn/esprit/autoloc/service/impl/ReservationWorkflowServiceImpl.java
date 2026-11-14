package tn.esprit.autoloc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.ReservationResponse;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.mapper.ReservationMapper;
import tn.esprit.autoloc.repository.ContratRepository;
import tn.esprit.autoloc.repository.ReservationRepository;
import tn.esprit.autoloc.service.IReservationWorkflowService;
import tn.esprit.autoloc.service.support.EntityLoader;

/** SRP : uniquement les transitions de statut d'une réservation. */
@Service
@Transactional
@RequiredArgsConstructor
public class ReservationWorkflowServiceImpl implements IReservationWorkflowService {

    private final ReservationRepository reservationRepository;
    private final ContratRepository contratRepository;
    private final ReservationMapper mapper;
    private final EntityLoader loader;

    @Override
    public ReservationResponse confirmer(Long id) {
        Reservation reservation = charger(id);
        if (reservation.getStatut() != StatutReservation.EN_ATTENTE) {
            throw new BusinessRuleException("Seule une réservation EN_ATTENTE peut être confirmée (statut actuel : "
                    + reservation.getStatut() + ")");
        }
        reservation.setStatut(StatutReservation.CONFIRMEE);
        return mapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    public ReservationResponse annuler(Long id) {
        Reservation reservation = charger(id);
        if (reservation.getStatut() == StatutReservation.TERMINEE
                || reservation.getStatut() == StatutReservation.ANNULEE) {
            throw new BusinessRuleException("Impossible d'annuler une réservation " + reservation.getStatut());
        }
        reservation.setStatut(StatutReservation.ANNULEE);
        // si un contrat existe, il n'est plus valide et le véhicule est libéré
        contratRepository.findByReservation_IdReservation(id).ifPresent(c -> {
            c.setValide(false);
            liberer(reservation.getVehicule());
        });
        return mapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    public ReservationResponse terminer(Long id) {
        Reservation reservation = charger(id);
        if (reservation.getStatut() != StatutReservation.CONFIRMEE) {
            throw new BusinessRuleException("Seule une réservation CONFIRMEE peut être terminée");
        }
        reservation.setStatut(StatutReservation.TERMINEE);
        liberer(reservation.getVehicule());
        return mapper.toResponse(reservationRepository.save(reservation));
    }

    private void liberer(Vehicule vehicule) {
        if (vehicule.getStatut() == StatutVehicule.LOUE) {
            vehicule.setStatut(StatutVehicule.DISPONIBLE);
        }
    }

    private Reservation charger(Long id) {
        return loader.charger(reservationRepository, id, "Reservation");
    }
}
