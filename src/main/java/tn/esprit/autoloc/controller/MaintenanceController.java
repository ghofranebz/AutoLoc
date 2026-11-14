package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.dto.MaintenanceRequest;
import tn.esprit.autoloc.dto.MaintenanceResponse;
import tn.esprit.autoloc.service.IMaintenanceService;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Maintenances", description = "Interventions de maintenance sur les véhicules")
@RestController
@RequestMapping("/api/maintenances")
@RequiredArgsConstructor
public class MaintenanceController {

    private final IMaintenanceService maintenanceService;

    @Operation(summary = "Lister toutes les maintenances")
    @GetMapping
    public List<MaintenanceResponse> getAll() {
        return maintenanceService.getAll();
    }

    @Operation(summary = "Consulter une maintenance par son id")
    @GetMapping("/{id}")
    public MaintenanceResponse getById(@PathVariable("id") Long id) {
        return maintenanceService.getById(id);
    }

    @Operation(summary = "Démarrer une maintenance sur un véhicule",
            description = "Le véhicule passe à MAINTENANCE. 400 s'il est loué ou déjà en maintenance.")
    @PostMapping("/vehicule/{idVehicule}")
    @ResponseStatus(HttpStatus.CREATED)
    public MaintenanceResponse demarrer(@PathVariable("idVehicule") Long idVehicule,
                                        @Valid @RequestBody MaintenanceRequest request) {
        return maintenanceService.demarrer(idVehicule, request);
    }

    @Operation(summary = "Terminer une maintenance (date de fin optionnelle, aujourd'hui par défaut)",
            description = "Le véhicule redevient DISPONIBLE.")
    @PatchMapping("/{id}/terminer")
    public MaintenanceResponse terminer(
            @PathVariable("id") Long id,
            @RequestParam(value = "dateFin", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        return maintenanceService.terminer(id, dateFin);
    }

    @Operation(summary = "Modifier la description / date de début d'une maintenance")
    @PutMapping("/{id}")
    public MaintenanceResponse update(@PathVariable("id") Long id, @Valid @RequestBody MaintenanceRequest request) {
        return maintenanceService.update(id, request);
    }

    @Operation(summary = "Supprimer une maintenance")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        maintenanceService.delete(id);
    }

    @Operation(summary = "Lister les maintenances en cours (sans date de fin)")
    @GetMapping("/en-cours")
    public List<MaintenanceResponse> enCours() {
        return maintenanceService.enCours();
    }

    @Operation(summary = "Lister les maintenances d'un véhicule")
    @GetMapping("/vehicule/{idVehicule}")
    public List<MaintenanceResponse> findByVehicule(@PathVariable("idVehicule") Long idVehicule) {
        return maintenanceService.findByVehicule(idVehicule);
    }

    @Operation(summary = "Lister les maintenances débutant entre deux dates (format yyyy-MM-dd)")
    @GetMapping("/periode")
    public List<MaintenanceResponse> findEntre(
            @RequestParam("debut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return maintenanceService.findEntre(debut, fin);
    }
}
