package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.dto.PeriodeRequest;
import tn.esprit.autoloc.dto.ReservationRequest;
import tn.esprit.autoloc.dto.ReservationResponse;
import tn.esprit.autoloc.service.IReservationService;
import tn.esprit.autoloc.service.IReservationWorkflowService;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Réservations", description = "Réservations de véhicules et cycle de vie (EN_ATTENTE, CONFIRMEE, TERMINEE, ANNULEE)")
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final IReservationService reservationService;
    private final IReservationWorkflowService workflowService;

    @Operation(summary = "Lister toutes les réservations")
    @GetMapping
    public List<ReservationResponse> getAll() {
        return reservationService.getAll();
    }

    @Operation(summary = "Consulter une réservation par son id")
    @GetMapping("/{id}")
    public ReservationResponse getById(@PathVariable("id") Long id) {
        return reservationService.getById(id);
    }

    @Operation(summary = "Créer une réservation (statut EN_ATTENTE)",
            description = "404 si client/véhicule inconnu, 400 si véhicule en maintenance ou dates invalides, 409 si chevauchement.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse creer(@Valid @RequestBody ReservationRequest request) {
        return reservationService.creer(request);
    }

    @Operation(summary = "Modifier les dates d'une réservation EN_ATTENTE")
    @PutMapping("/{id}/dates")
    public ReservationResponse modifierDates(@PathVariable("id") Long id, @Valid @RequestBody PeriodeRequest periode) {
        return reservationService.modifierDates(id, periode);
    }

    @Operation(summary = "Supprimer une réservation", description = "Refusé (400) si un contrat y est lié.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        reservationService.delete(id);
    }

    @Operation(summary = "Lister les réservations d'un client")
    @GetMapping("/client/{idClient}")
    public List<ReservationResponse> findByClient(@PathVariable("idClient") Long idClient) {
        return reservationService.findByClient(idClient);
    }

    @Operation(summary = "Lister les réservations d'un véhicule")
    @GetMapping("/vehicule/{idVehicule}")
    public List<ReservationResponse> findByVehicule(@PathVariable("idVehicule") Long idVehicule) {
        return reservationService.findByVehicule(idVehicule);
    }

    @Operation(summary = "Lister les réservations par statut (triées par date de début)")
    @GetMapping("/statut/{statut}")
    public List<ReservationResponse> findByStatut(@PathVariable("statut") StatutReservation statut) {
        return reservationService.findByStatut(statut);
    }

    @Operation(summary = "Lister les réservations commençant entre deux dates (format yyyy-MM-dd)")
    @GetMapping("/periode")
    public List<ReservationResponse> findEntre(
            @RequestParam("debut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return reservationService.findEntre(debut, fin);
    }

    @Operation(summary = "Confirmer une réservation (EN_ATTENTE -> CONFIRMEE)")
    @PatchMapping("/{id}/confirmer")
    public ReservationResponse confirmer(@PathVariable("id") Long id) {
        return workflowService.confirmer(id);
    }

    @Operation(summary = "Annuler une réservation", description = "Invalide le contrat éventuel et libère le véhicule.")
    @PatchMapping("/{id}/annuler")
    public ReservationResponse annuler(@PathVariable("id") Long id) {
        return workflowService.annuler(id);
    }

    @Operation(summary = "Terminer une réservation (CONFIRMEE -> TERMINEE) et libérer le véhicule")
    @PatchMapping("/{id}/terminer")
    public ReservationResponse terminer(@PathVariable("id") Long id) {
        return workflowService.terminer(id);
    }
}
