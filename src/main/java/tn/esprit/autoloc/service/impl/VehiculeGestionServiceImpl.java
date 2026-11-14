package tn.esprit.autoloc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.VehiculeResponse;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.mapper.VehiculeMapper;
import tn.esprit.autoloc.repository.AgenceRepository;
import tn.esprit.autoloc.repository.EquipementRepository;
import tn.esprit.autoloc.repository.VehiculeRepository;
import tn.esprit.autoloc.service.IVehiculeGestionService;
import tn.esprit.autoloc.service.support.EntityLoader;

/** SRP : gestion d'un véhicule existant, séparée du CRUD pour que chaque classe n'ait qu'une raison de changer. */
@Service
@Transactional
@RequiredArgsConstructor
public class VehiculeGestionServiceImpl implements IVehiculeGestionService {

    private final VehiculeRepository vehiculeRepository;
    private final AgenceRepository agenceRepository;
    private final EquipementRepository equipementRepository;
    private final VehiculeMapper mapper;
    private final EntityLoader loader;

    @Override
    public VehiculeResponse affecterAgence(Long idVehicule, Long idAgence) {
        Vehicule vehicule = charger(idVehicule);
        vehicule.setAgence(loader.charger(agenceRepository, idAgence, "Agence"));
        return mapper.toResponse(vehiculeRepository.save(vehicule));
    }

    @Override
    public VehiculeResponse ajouterEquipement(Long idVehicule, Long idEquipement) {
        Vehicule vehicule = charger(idVehicule);
        vehicule.getEquipements().add(loader.charger(equipementRepository, idEquipement, "Equipement"));
        return mapper.toResponse(vehiculeRepository.save(vehicule));
    }

    @Override
    public VehiculeResponse retirerEquipement(Long idVehicule, Long idEquipement) {
        Vehicule vehicule = charger(idVehicule);
        Equipement equipement = loader.charger(equipementRepository, idEquipement, "Equipement");
        if (!vehicule.getEquipements().remove(equipement)) {
            throw new BusinessRuleException("Le véhicule " + idVehicule + " ne possède pas l'équipement " + idEquipement);
        }
        return mapper.toResponse(vehiculeRepository.save(vehicule));
    }

    @Override
    public VehiculeResponse changerStatut(Long idVehicule, StatutVehicule statut) {
        Vehicule vehicule = charger(idVehicule);
        vehicule.setStatut(statut);
        return mapper.toResponse(vehiculeRepository.save(vehicule));
    }

    private Vehicule charger(Long id) {
        return loader.charger(vehiculeRepository, id, "Vehicule");
    }
}
