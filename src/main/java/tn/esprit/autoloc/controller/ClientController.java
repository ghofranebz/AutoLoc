package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.dto.ClientRequest;
import tn.esprit.autoloc.dto.ClientResponse;
import tn.esprit.autoloc.service.IClientService;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Clients", description = "Gestion des clients")
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final IClientService clientService;

    @Operation(summary = "Lister tous les clients")
    @GetMapping
    public List<ClientResponse> getAll() {
        return clientService.getAll();
    }

    @Operation(summary = "Consulter un client par son id")
    @GetMapping("/{id}")
    public ClientResponse getById(@PathVariable("id") Long id) {
        return clientService.getById(id);
    }

    @Operation(summary = "Créer un client", description = "409 si l'email ou le numéro de permis existe déjà.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponse add(@Valid @RequestBody ClientRequest request) {
        return clientService.add(request);
    }

    @Operation(summary = "Modifier un client")
    @PutMapping("/{id}")
    public ClientResponse update(@PathVariable("id") Long id, @Valid @RequestBody ClientRequest request) {
        return clientService.update(id, request);
    }

    @Operation(summary = "Supprimer un client", description = "Refusé (400) si le client a des réservations.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        clientService.delete(id);
    }

    @Operation(summary = "Consulter un client par email")
    @GetMapping("/email/{email}")
    public ClientResponse getByEmail(@PathVariable("email") String email) {
        return clientService.getByEmail(email);
    }

    @Operation(summary = "Consulter un client par numéro de permis")
    @GetMapping("/permis/{numPermis}")
    public ClientResponse getByNumPermis(@PathVariable("numPermis") String numPermis) {
        return clientService.getByNumPermis(numPermis);
    }

    @Operation(summary = "Rechercher des clients par mot-clé (nom ou prénom)")
    @GetMapping("/recherche")
    public List<ClientResponse> search(@RequestParam("motCle") String motCle) {
        return clientService.search(motCle);
    }

    @Operation(summary = "Lister les clients inscrits après une date (format yyyy-MM-dd)")
    @GetMapping("/inscrits-apres")
    public List<ClientResponse> inscritsApres(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return clientService.inscritsApres(date);
    }
}
