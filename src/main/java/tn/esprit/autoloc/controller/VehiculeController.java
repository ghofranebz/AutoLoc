package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.dto.VehiculeRequest;
import tn.esprit.autoloc.dto.VehiculeResponse;
import tn.esprit.autoloc.service.IVehiculeGestionService;
import tn.esprit.autoloc.service.IVehiculeService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Deux interfaces de service injectées (ISP) : CRUD/recherche d'un côté, gestion de l'autre. */
@Tag(name = "Véhicules", description = "Gestion de la flotte de véhicules")
@RestController
@RequestMapping("/api/vehicules")
@RequiredArgsConstructor
public class VehiculeController {

    private final IVehiculeService vehiculeService;
    private final IVehiculeGestionService gestionService;

    @Operation(summary = "Lister tous les véhicules")
    @GetMapping
    public List<VehiculeResponse> getAll() {
        return vehiculeService.getAll();
    }

    @Operation(summary = "Consulter un véhicule par son id (avec ses équipements)")
    @GetMapping("/{id}")
    public VehiculeResponse getById(@PathVariable("id") Long id) {
        return vehiculeService.getById(id);
    }

    @Operation(summary = "Créer un véhicule", description = "Statut DISPONIBLE par défaut ; 409 si l'immatriculation existe déjà.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehiculeResponse add(@Valid @RequestBody VehiculeRequest request) {
        return vehiculeService.add(request);
    }

    @Operation(summary = "Modifier un véhicule")
    @PutMapping("/{id}")
    public VehiculeResponse update(@PathVariable("id") Long id, @Valid @RequestBody VehiculeRequest request) {
        return vehiculeService.update(id, request);
    }

    @Operation(summary = "Supprimer un véhicule", description = "Refusé (400) s'il a des réservations ou des maintenances.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        vehiculeService.delete(id);
    }

    @Operation(summary = "Consulter un véhicule par immatriculation")
    @GetMapping("/immatriculation/{immatriculation}")
    public VehiculeResponse getByImmatriculation(@PathVariable("immatriculation") String immatriculation) {
        return vehiculeService.getByImmatriculation(immatriculation);
    }

    @Operation(summary = "Rechercher par statut et/ou catégorie (filtres optionnels)")
    @GetMapping("/recherche")
    public List<VehiculeResponse> rechercher(
            @RequestParam(value = "statut", required = false) StatutVehicule statut,
            @RequestParam(value = "categorie", required = false) CategorieVehicule categorie) {
        return vehiculeService.rechercher(statut, categorie);
    }

    @Operation(summary = "Lister les véhicules d'une agence")
    @GetMapping("/agence/{idAgence}")
    public List<VehiculeResponse> findByAgence(@PathVariable("idAgence") Long idAgence) {
        return vehiculeService.findByAgence(idAgence);
    }

    @Operation(summary = "Lister les véhicules dont le tarif journalier est entre min et max")
    @GetMapping("/tarif")
    public List<VehiculeResponse> findByTarif(@RequestParam("min") BigDecimal min,
                                              @RequestParam("max") BigDecimal max) {
        return vehiculeService.findByTarif(min, max);
    }

    @Operation(summary = "Lister les véhicules disponibles sur une période (format yyyy-MM-dd)")
    @GetMapping("/disponibles")
    public List<VehiculeResponse> findDisponibles(
            @RequestParam("dateDebut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam("dateFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        return vehiculeService.findDisponibles(dateDebut, dateFin);
    }

    @Operation(summary = "Affecter un véhicule à une agence")
    @PatchMapping("/{idVehicule}/agence/{idAgence}")
    public VehiculeResponse affecterAgence(@PathVariable("idVehicule") Long idVehicule,
                                           @PathVariable("idAgence") Long idAgence) {
        return gestionService.affecterAgence(idVehicule, idAgence);
    }

    @Operation(summary = "Ajouter un équipement à un véhicule")
    @PostMapping("/{idVehicule}/equipements/{idEquipement}")
    public VehiculeResponse ajouterEquipement(@PathVariable("idVehicule") Long idVehicule,
                                              @PathVariable("idEquipement") Long idEquipement) {
        return gestionService.ajouterEquipement(idVehicule, idEquipement);
    }

    @Operation(summary = "Retirer un équipement d'un véhicule")
    @DeleteMapping("/{idVehicule}/equipements/{idEquipement}")
    public VehiculeResponse retirerEquipement(@PathVariable("idVehicule") Long idVehicule,
                                              @PathVariable("idEquipement") Long idEquipement) {
        return gestionService.retirerEquipement(idVehicule, idEquipement);
    }

    @Operation(summary = "Changer le statut d'un véhicule (DISPONIBLE, LOUE, MAINTENANCE)")
    @PatchMapping("/{idVehicule}/statut")
    public VehiculeResponse changerStatut(@PathVariable("idVehicule") Long idVehicule,
                                          @RequestParam("statut") StatutVehicule statut) {
        return gestionService.changerStatut(idVehicule, statut);
    }
}
