package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.dto.EquipementRequest;
import tn.esprit.autoloc.dto.EquipementResponse;
import tn.esprit.autoloc.service.IEquipementService;

import java.util.List;

@Tag(name = "Équipements", description = "Catalogue des équipements (GPS, siège bébé...)")
@RestController
@RequestMapping("/api/equipements")
@RequiredArgsConstructor
public class EquipementController {

    private final IEquipementService equipementService;

    @Operation(summary = "Lister tous les équipements")
    @GetMapping
    public List<EquipementResponse> getAll() {
        return equipementService.getAll();
    }

    @Operation(summary = "Consulter un équipement par son id")
    @GetMapping("/{id}")
    public EquipementResponse getById(@PathVariable("id") Long id) {
        return equipementService.getById(id);
    }

    @Operation(summary = "Créer un équipement", description = "409 si le libellé existe déjà.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EquipementResponse add(@Valid @RequestBody EquipementRequest request) {
        return equipementService.add(request);
    }

    @Operation(summary = "Modifier un équipement")
    @PutMapping("/{id}")
    public EquipementResponse update(@PathVariable("id") Long id, @Valid @RequestBody EquipementRequest request) {
        return equipementService.update(id, request);
    }

    @Operation(summary = "Supprimer un équipement (il est retiré de tous les véhicules)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        equipementService.delete(id);
    }

    @Operation(summary = "Consulter un équipement par libellé")
    @GetMapping("/libelle/{libelle}")
    public EquipementResponse getByLibelle(@PathVariable("libelle") String libelle) {
        return equipementService.getByLibelle(libelle);
    }
}
