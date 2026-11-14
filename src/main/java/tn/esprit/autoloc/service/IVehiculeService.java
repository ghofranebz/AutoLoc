package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.dto.VehiculeRequest;
import tn.esprit.autoloc.dto.VehiculeResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** CRUD et recherches. Les opérations d'affectation / équipements / statut sont dans {@link IVehiculeGestionService}. */
public interface IVehiculeService extends Consultable<VehiculeResponse, Long>,
        Modifiable<VehiculeRequest, VehiculeResponse, Long>, Supprimable<Long> {

    VehiculeResponse add(VehiculeRequest request);

    VehiculeResponse getByImmatriculation(String immatriculation);

    List<VehiculeResponse> rechercher(StatutVehicule statut, CategorieVehicule categorie);

    List<VehiculeResponse> findByAgence(Long idAgence);

    List<VehiculeResponse> findByTarif(BigDecimal min, BigDecimal max);

    List<VehiculeResponse> findDisponibles(LocalDate dateDebut, LocalDate dateFin);
}
