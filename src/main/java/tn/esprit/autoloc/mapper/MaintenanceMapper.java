package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.dto.MaintenanceRequest;
import tn.esprit.autoloc.dto.MaintenanceResponse;

@Component
public class MaintenanceMapper {

    public Maintenance toEntity(MaintenanceRequest r) {
        Maintenance m = new Maintenance();
        m.setDateDebut(r.dateDebut());
        m.setDescription(r.description());
        return m;
    }

    public MaintenanceResponse toResponse(Maintenance m) {
        Long idVehicule = m.getVehicule() != null ? m.getVehicule().getIdVehicule() : null;
        return new MaintenanceResponse(m.getIdMaintenance(), m.getDateDebut(), m.getDateFin(), m.getDescription(),
                idVehicule);
    }
}
