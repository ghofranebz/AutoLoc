package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.autoloc.dto.SituationPaiementResponse;
import tn.esprit.autoloc.service.IFacturationService;

/**
 * ISP (cours, slide « ProjetController(ConsultationProjet) ») : ce contrôleur ne dépend que de
 * {@link IFacturationService}, l'interface qui correspond exactement à son besoin.
 */
@Tag(name = "Facturation", description = "Situation de paiement d'un contrat")
@RestController
@RequestMapping("/api/contrats")
@RequiredArgsConstructor
public class FacturationController {

    private final IFacturationService facturationService;

    @Operation(summary = "Situation de paiement : montant total, déjà payé, reste à payer")
    @GetMapping("/{id}/situation-paiement")
    public SituationPaiementResponse situationPaiement(@PathVariable("id") Long id) {
        return facturationService.situationPaiement(id);
    }
}
