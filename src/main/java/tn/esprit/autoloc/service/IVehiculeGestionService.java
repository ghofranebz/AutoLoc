package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.dto.VehiculeResponse;

/** ISP : opérations de gestion d'un véhicule existant (affectation, équipements, statut). */
public interface IVehiculeGestionService {

    /** Affectation @ManyToOne : rattache un véhicule à une agence. */
    VehiculeResponse affecterVehicule(Long idVehicule, Long idAgence);

    /** Affectation @ManyToMany : installe un équipement sur un véhicule. */
    VehiculeResponse affecterEquipement(Long idVehicule, Long idEquipement);

    /** Désaffectation @ManyToMany : retire un équipement d'un véhicule (l'équipement n'est pas supprimé). */
    VehiculeResponse desaffecterEquipement(Long idVehicule, Long idEquipement);

    VehiculeResponse changerStatut(Long idVehicule, StatutVehicule statut);
}