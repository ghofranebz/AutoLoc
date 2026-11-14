package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.dto.AgenceRequest;
import tn.esprit.autoloc.dto.AgenceResponse;
import tn.esprit.autoloc.service.IAgenceService;

import java.util.List;

/**
 * Couche présentation : reçoit la requête HTTP, délègue au service (injecté par constructeur,
 * via l'INTERFACE : DIP) et renvoie un DTO sérialisé en JSON.
 * URL de base : http://localhost:8081/api/agences
 */
@Tag(name = "Agences", description = "Gestion des agences de location")
@RestController
@RequestMapping("/api/agences")
@RequiredArgsConstructor
public class AgenceController {

    private final IAgenceService agenceService;

    @Operation(summary = "Lister toutes les agences")
    @GetMapping
    public List<AgenceResponse> getAll() {
        return agenceService.getAll();
    }

    @Operation(summary = "Consulter une agence par son id")
    @GetMapping("/{id}")
    public AgenceResponse getById(@PathVariable("id") Long id) {
        return agenceService.getById(id);
    }

    @Operation(summary = "Créer une agence")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgenceResponse add(@Valid @RequestBody AgenceRequest request) {
        return agenceService.add(request);
    }

    @Operation(summary = "Modifier une agence")
    @PutMapping("/{id}")
    public AgenceResponse update(@PathVariable("id") Long id, @Valid @RequestBody AgenceRequest request) {
        return agenceService.update(id, request);
    }

    @Operation(summary = "Supprimer une agence", description = "Refusé (400) si l'agence possède encore des véhicules ou des employés.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        agenceService.delete(id);
    }

    @Operation(summary = "Lister les agences d'une ville (insensible à la casse)")
    @GetMapping("/ville/{ville}")
    public List<AgenceResponse> findByVille(@PathVariable("ville") String ville) {
        return agenceService.findByVille(ville);
    }

    @Operation(summary = "Rechercher des agences par nom (contient, insensible à la casse)")
    @GetMapping("/recherche")
    public List<AgenceResponse> searchByNom(@RequestParam("nom") String nom) {
        return agenceService.searchByNom(nom);
    }
}
