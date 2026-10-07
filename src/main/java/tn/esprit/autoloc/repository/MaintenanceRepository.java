package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Maintenance;

import java.time.LocalDate;
import java.util.List;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    List<Maintenance> findByVehicule_IdVehicule(Long idVehicule);

    // Maintenances en cours : dateFin non renseignée
    List<Maintenance> findByDateFinIsNull();

    List<Maintenance> findByDateDebutBetween(LocalDate debut, LocalDate fin);
}
