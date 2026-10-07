package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    Optional<Vehicule> findByImmatriculation(String immatriculation);

    boolean existsByImmatriculation(String immatriculation);

    List<Vehicule> findByStatut(StatutVehicule statut);

    List<Vehicule> findByCategorie(CategorieVehicule categorie);

    List<Vehicule> findByStatutAndCategorie(StatutVehicule statut, CategorieVehicule categorie);

    // "_" = traversée de la relation : vehicule.agence.idAgence
    List<Vehicule> findByAgence_IdAgence(Long idAgence);

    List<Vehicule> findByTarifJournalierBetween(BigDecimal min, BigDecimal max);

    // Charge le véhicule ET ses équipements en une seule requête (évite LazyInitializationException)
    @EntityGraph(attributePaths = "equipements")
    Optional<Vehicule> findWithEquipementsByIdVehicule(Long idVehicule);

    // Véhicules DISPONIBLES sans réservation active qui chevauche la période demandée
    @Query("""
            select v from Vehicule v
            where v.statut = tn.esprit.autoloc.domain.StatutVehicule.DISPONIBLE
              and not exists (
                  select r.idReservation from Reservation r
                  where r.vehicule = v
                    and r.statut <> tn.esprit.autoloc.domain.StatutReservation.ANNULEE
                    and r.dateDebut <= :dateFin
                    and r.dateFin >= :dateDebut)
            """)
    List<Vehicule> findDisponiblesEntre(@Param("dateDebut") LocalDate dateDebut,
                                        @Param("dateFin") LocalDate dateFin);
}
