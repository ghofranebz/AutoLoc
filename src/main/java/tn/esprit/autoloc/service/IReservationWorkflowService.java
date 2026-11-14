package tn.esprit.autoloc.service;

import tn.esprit.autoloc.dto.ReservationResponse;

/** ISP : transitions de statut d'une réservation (EN_ATTENTE -> CONFIRMEE -> TERMINEE, ou ANNULEE). */
public interface IReservationWorkflowService {

    ReservationResponse confirmer(Long id);

    ReservationResponse annuler(Long id);

    ReservationResponse terminer(Long id);
}
