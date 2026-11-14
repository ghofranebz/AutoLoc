package tn.esprit.autoloc.service;

import tn.esprit.autoloc.dto.MaintenanceRequest;
import tn.esprit.autoloc.dto.MaintenanceResponse;

import java.time.LocalDate;
import java.util.List;

public interface IMaintenanceService extends Consultable<MaintenanceResponse, Long>,
        Modifiable<MaintenanceRequest, MaintenanceResponse, Long>, Supprimable<Long> {

    MaintenanceResponse demarrer(Long idVehicule, MaintenanceRequest request);

    MaintenanceResponse terminer(Long id, LocalDate dateFin);

    List<MaintenanceResponse> enCours();

    List<MaintenanceResponse> findByVehicule(Long idVehicule);

    List<MaintenanceResponse> findEntre(LocalDate debut, LocalDate fin);
}
