package tn.esprit.autoloc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.VehiculeResponse;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.exception.ConflictException;
import tn.esprit.autoloc.mapper.VehiculeMapper;
import tn.esprit.autoloc.repository.AgenceRepository;
import tn.esprit.autoloc.repository.EquipementRepository;
import tn.esprit.autoloc.repository.VehiculeRepository;
import tn.esprit.autoloc.service.IVehiculeGestionService;
import tn.esprit.autoloc.service.support.EntityLoader;

/**
 * Chapitre 9 : chaque opération récupère les entités en base, modifie le lien depuis le côté
 * PROPRIÉTAIRE (Vehicule), puis sauvegarde, le tout dans une transaction (@Transactional).
 */
@Service
@Transactional
@RequiredArgsConstructor
public class VehiculeGestionServiceImpl implements IVehiculeGestionService {

    private final VehiculeRepository vehiculeRepository;
    private final AgenceRepository agenceRepository;
    private final EquipementRepository equipementRepository;
    private final VehiculeMapper mapper;
    private final EntityLoader loader;

    /**
     * Scénario 2 (@ManyToOne Vehicule -> Agence) : un véhicule neuf est rattaché à une agence.
     * Vehicule est le côté propriétaire (clé étrangère id_agence) : on fait vehicule.setAgence(agence).
     */
    @Override
    public VehiculeResponse affecterVehicule(Long idVehicule, Long idAgence) {
        Vehicule vehicule = charger(idVehicule);
        Agence agence = loader.charger(agenceRepository, idAgence, "Agence");
        vehicule.setAgence(agence);
        return mapper.toResponse(vehiculeRepository.save(vehicule));
    }

    /**
     * Scénario 3 (@ManyToMany Vehicule <-> Equipement) : l'agent installe un siège bébé.
     * Vehicule est le côté propriétaire (@JoinTable vehicule_equipement, Equipement a mappedBy) :
     * on ajoute l'équipement à vehicule.getEquipements(). Une ligne est insérée dans la table de jointure.
     */
    @Override
    public VehiculeResponse affecterEquipement(Long idVehicule, Long idEquipement) {
        Vehicule vehicule = charger(idVehicule);
        Equipement equipement = loader.charger(equipementRepository, idEquipement, "Equipement");
        if (vehicule.getEquipements().contains(equipement)) {
            throw new ConflictException("L'équipement " + idEquipement + " est déjà installé sur le véhicule " + idVehicule);
        }
        vehicule.getEquipements().add(equipement);
        return mapper.toResponse(vehiculeRepository.save(vehicule));
    }

    /**
     * Scénario 3 : l'agent retire le siège bébé au retour du véhicule.
     * On retire l'équipement de la collection du côté propriétaire : la ligne de la table de jointure
     * est supprimée, mais ni le véhicule ni l'équipement ne sont supprimés.
     */
    @Override
    public VehiculeResponse desaffecterEquipement(Long idVehicule, Long idEquipement) {
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