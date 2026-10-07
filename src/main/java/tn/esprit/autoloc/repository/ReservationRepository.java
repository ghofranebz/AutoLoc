package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByClient_IdClient(Long idClient);

    List<Reservation> findByVehicule_IdVehicule(Long idVehicule);

    List<Reservation> findByStatut(StatutReservation statut);

    List<Reservation> findByStatutOrderByDateDebutAsc(StatutReservation statut);

    List<Reservation> findByDateDebutBetween(LocalDate debut, LocalDate fin);

    // true si le véhicule a déjà une réservation non annulée qui chevauche la période
    @Query("""
            select count(r) > 0 from Reservation r
            where r.vehicule.idVehicule = :idVehicule
              and r.statut <> tn.esprit.autoloc.domain.StatutReservation.ANNULEE
              and r.dateDebut <= :dateFin
              and r.dateFin >= :dateDebut
            """)
    boolean existsChevauchement(@Param("idVehicule") Long idVehicule,
                                @Param("dateDebut") LocalDate dateDebut,
                                @Param("dateFin") LocalDate dateFin);
}
