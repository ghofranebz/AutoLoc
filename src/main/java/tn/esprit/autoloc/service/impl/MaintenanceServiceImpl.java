package tn.esprit.autoloc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.MaintenanceRequest;
import tn.esprit.autoloc.dto.MaintenanceResponse;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.mapper.MaintenanceMapper;
import tn.esprit.autoloc.repository.MaintenanceRepository;
import tn.esprit.autoloc.repository.VehiculeRepository;
import tn.esprit.autoloc.service.IMaintenanceService;
import tn.esprit.autoloc.service.support.EntityLoader;
import tn.esprit.autoloc.service.support.Fuseau;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MaintenanceServiceImpl implements IMaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final VehiculeRepository vehiculeRepository;
    private final MaintenanceMapper mapper;
    private final EntityLoader loader;

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceResponse> getAll() {
        return maintenanceRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceResponse getById(Long id) {
        return mapper.toResponse(charger(id));
    }

    /** Ouvre une maintenance : le véhicule passe au statut MAINTENANCE. */
    @Override
    public MaintenanceResponse demarrer(Long idVehicule, MaintenanceRequest request) {
        Vehicule vehicule = loader.charger(vehiculeRepository, idVehicule, "Vehicule");
        if (vehicule.getStatut() == StatutVehicule.LOUE) {
            throw new BusinessRuleException("Le véhicule " + idVehicule + " est actuellement loué");
        }
        if (vehicule.getStatut() == StatutVehicule.MAINTENANCE) {
            throw new BusinessRuleException("Le véhicule " + idVehicule + " est déjà en maintenance");
        }
        Maintenance maintenance = mapper.toEntity(request);
        if (maintenance.getDateDebut() == null) {
            maintenance.setDateDebut(LocalDate.now(Fuseau.TUNIS));        }
        maintenance.setVehicule(vehicule);
        vehicule.setStatut(StatutVehicule.MAINTENANCE);
        return mapper.toResponse(maintenanceRepository.save(maintenance));
    }

    /** Clôture la maintenance : le véhicule redevient DISPONIBLE. */
    @Override
    public MaintenanceResponse terminer(Long id, LocalDate dateFin) {
        Maintenance maintenance = charger(id);
        if (maintenance.getDateFin() != null) {
            throw new BusinessRuleException("La maintenance " + id + " est déjà terminée");
        }
        LocalDate fin = dateFin != null ? dateFin : LocalDate.now(Fuseau.TUNIS);        if (fin.isBefore(maintenance.getDateDebut())) {
            throw new BusinessRuleException("La date de fin ne peut pas précéder la date de début");
        }
        maintenance.setDateFin(fin);
        maintenance.getVehicule().setStatut(StatutVehicule.DISPONIBLE);
        return mapper.toResponse(maintenanceRepository.save(maintenance));
    }

    @Override
    public MaintenanceResponse update(Long id, MaintenanceRequest request) {
        Maintenance existante = charger(id);
        if (request.dateDebut() != null) {
            existante.setDateDebut(request.dateDebut());
        }
        existante.setDescription(request.description());
        return mapper.toResponse(maintenanceRepository.save(existante));
    }

    @Override
    public void delete(Long id) {
        Maintenance maintenance = charger(id);
        if (maintenance.getDateFin() == null
                && maintenance.getVehicule().getStatut() == StatutVehicule.MAINTENANCE) {
            maintenance.getVehicule().setStatut(StatutVehicule.DISPONIBLE);
        }
        maintenanceRepository.delete(maintenance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceResponse> enCours() {
        return maintenanceRepository.findByDateFinIsNull().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceResponse> findByVehicule(Long idVehicule) {
        return maintenanceRepository.findByVehicule_IdVehicule(idVehicule).stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceResponse> findEntre(LocalDate debut, LocalDate fin) {
        return maintenanceRepository.findByDateDebutBetween(debut, fin).stream().map(mapper::toResponse).toList();
    }

    private Maintenance charger(Long id) {
        return loader.charger(maintenanceRepository, id, "Maintenance");
    }
}
