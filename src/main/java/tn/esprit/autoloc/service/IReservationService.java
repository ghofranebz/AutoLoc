package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.dto.PeriodeRequest;
import tn.esprit.autoloc.dto.ReservationRequest;
import tn.esprit.autoloc.dto.ReservationResponse;

import java.time.LocalDate;
import java.util.List;

/** Création, modification, suppression et consultation. Le cycle de vie est dans {@link IReservationWorkflowService}. */
public interface IReservationService extends Consultable<ReservationResponse, Long>, Supprimable<Long> {

    ReservationResponse creer(ReservationRequest request);

    ReservationResponse modifierDates(Long id, PeriodeRequest periode);

    List<ReservationResponse> findByClient(Long idClient);

    List<ReservationResponse> findByVehicule(Long idVehicule);

    List<ReservationResponse> findByStatut(StatutReservation statut);

    List<ReservationResponse> findEntre(LocalDate debut, LocalDate fin);
}
