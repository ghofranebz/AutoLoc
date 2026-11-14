package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.dto.ContratResponse;
import tn.esprit.autoloc.service.IContratService;

import java.time.LocalDate;
import java.util.List;

/** ISP : ce contrôleur dépend uniquement de IContratService (cycle de vie), pas de la facturation. */
@Tag(name = "Contrats", description = "Contrats de location générés à partir de réservations confirmées")
@RestController
@RequestMapping("/api/contrats")
@RequiredArgsConstructor
public class ContratController {

    private final IContratService contratService;

    @Operation(summary = "Lister tous les contrats")
    @GetMapping
    public List<ContratResponse> getAll() {
        return contratService.getAll();
    }

    @Operation(summary = "Consulter un contrat par son id")
    @GetMapping("/{id}")
    public ContratResponse getById(@PathVariable("id") Long id) {
        return contratService.getById(id);
    }

    @Operation(summary = "Créer le contrat d'une réservation CONFIRMEE",
            description = "Le montant est calculé par la stratégie de tarification active (propriété autoloc.tarification) "
                    + "et le véhicule passe à LOUE. 400 si la réservation n'est pas CONFIRMEE, 409 si un contrat existe déjà.")
    @PostMapping("/reservation/{idReservation}")
    @ResponseStatus(HttpStatus.CREATED)
    public ContratResponse creer(@PathVariable("idReservation") Long idReservation) {
        return contratService.creer(idReservation);
    }

    @Operation(summary = "Valider ou invalider un contrat")
    @PatchMapping("/{id}/validite")
    public ContratResponse changerValidite(@PathVariable("id") Long id, @RequestParam("valide") boolean valide) {
        return contratService.changerValidite(id, valide);
    }

    @Operation(summary = "Supprimer un contrat (et ses paiements)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        contratService.delete(id);
    }

    @Operation(summary = "Consulter le contrat d'une réservation")
    @GetMapping("/reservation/{idReservation}")
    public ContratResponse getByReservation(@PathVariable("idReservation") Long idReservation) {
        return contratService.getByReservation(idReservation);
    }

    @Operation(summary = "Lister les contrats valides (true) ou invalides (false)")
    @GetMapping("/valide/{valide}")
    public List<ContratResponse> findByValide(@PathVariable("valide") boolean valide) {
        return contratService.findByValide(valide);
    }

    @Operation(summary = "Lister les contrats signés entre deux dates (format yyyy-MM-dd)")
    @GetMapping("/periode")
    public List<ContratResponse> findSigneEntre(
            @RequestParam("debut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return contratService.findSigneEntre(debut, fin);
    }
}
