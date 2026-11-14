package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.ModePaiement;
import tn.esprit.autoloc.dto.PaiementRequest;
import tn.esprit.autoloc.dto.PaiementResponse;
import tn.esprit.autoloc.service.IPaiementService;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Paiements", description = "Paiements rattachés aux contrats")
@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
public class PaiementController {

    private final IPaiementService paiementService;

    @Operation(summary = "Lister tous les paiements")
    @GetMapping
    public List<PaiementResponse> getAll() {
        return paiementService.getAll();
    }

    @Operation(summary = "Consulter un paiement par son id")
    @GetMapping("/{id}")
    public PaiementResponse getById(@PathVariable("id") Long id) {
        return paiementService.getById(id);
    }

    @Operation(summary = "Enregistrer un paiement sur un contrat",
            description = "400 si le contrat n'est pas valide ou si le montant dépasse le reste à payer.")
    @PostMapping("/contrat/{idContrat}")
    @ResponseStatus(HttpStatus.CREATED)
    public PaiementResponse ajouter(@PathVariable("idContrat") Long idContrat,
                                    @Valid @RequestBody PaiementRequest request) {
        return paiementService.ajouter(idContrat, request);
    }

    @Operation(summary = "Supprimer un paiement")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        paiementService.delete(id);
    }

    @Operation(summary = "Lister les paiements d'un contrat")
    @GetMapping("/contrat/{idContrat}")
    public List<PaiementResponse> findByContrat(@PathVariable("idContrat") Long idContrat) {
        return paiementService.findByContrat(idContrat);
    }

    @Operation(summary = "Lister les paiements par mode (CARTE, ESPECES, VIREMENT)")
    @GetMapping("/mode/{mode}")
    public List<PaiementResponse> findByMode(@PathVariable("mode") ModePaiement mode) {
        return paiementService.findByMode(mode);
    }

    @Operation(summary = "Lister les paiements effectués entre deux dates (format yyyy-MM-dd)")
    @GetMapping("/periode")
    public List<PaiementResponse> findEntre(
            @RequestParam("debut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return paiementService.findEntre(debut, fin);
    }
}
