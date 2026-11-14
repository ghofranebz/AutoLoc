package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.dto.ReservationRequest;
import tn.esprit.autoloc.dto.ReservationResponse;

@Component
public class ReservationMapper {

    /** Ne remplit que les dates : client, véhicule et statut sont décidés par le service. */
    public Reservation toEntity(ReservationRequest r) {
        Reservation res = new Reservation();
        res.setDateDebut(r.dateDebut());
        res.setDateFin(r.dateFin());
        return res;
    }

    public ReservationResponse toResponse(Reservation r) {
        Long idClient = r.getClient() != null ? r.getClient().getIdClient() : null;
        Long idVehicule = r.getVehicule() != null ? r.getVehicule().getIdVehicule() : null;
        return new ReservationResponse(r.getIdReservation(), r.getDateDebut(), r.getDateFin(), r.getStatut(),
                idClient, idVehicule);
    }
}
