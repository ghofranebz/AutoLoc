package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.dto.VehiculeResponse;

/** ISP : opérations de gestion d'un véhicule existant (affectation, équipements, statut). */
public interface IVehiculeGestionService {

    VehiculeResponse affecterAgence(Long idVehicule, Long idAgence);

    VehiculeResponse ajouterEquipement(Long idVehicule, Long idEquipement);

    VehiculeResponse retirerEquipement(Long idVehicule, Long idEquipement);

    VehiculeResponse changerStatut(Long idVehicule, StatutVehicule statut);
}
