package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.RoleEmploye;
import tn.esprit.autoloc.dto.EmployeRequest;
import tn.esprit.autoloc.dto.EmployeResponse;
import tn.esprit.autoloc.service.IEmployeService;

import java.util.List;

@Tag(name = "Employés", description = "Gestion des employés et de leur affectation aux agences")
@RestController
@RequestMapping("/api/employes")
@RequiredArgsConstructor
public class EmployeController {

    private final IEmployeService employeService;

    @Operation(summary = "Lister tous les employés")
    @GetMapping
    public List<EmployeResponse> getAll() {
        return employeService.getAll();
    }

    @Operation(summary = "Consulter un employé par son id")
    @GetMapping("/{id}")
    public EmployeResponse getById(@PathVariable("id") Long id) {
        return employeService.getById(id);
    }

    @Operation(summary = "Créer un employé", description = "idAgence est optionnel ; 404 si l'agence n'existe pas.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeResponse add(@Valid @RequestBody EmployeRequest request) {
        return employeService.add(request);
    }

    @Operation(summary = "Modifier un employé (nom, prénom, rôle)")
    @PutMapping("/{id}")
    public EmployeResponse update(@PathVariable("id") Long id, @Valid @RequestBody EmployeRequest request) {
        return employeService.update(id, request);
    }

    @Operation(summary = "Supprimer un employé")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        employeService.delete(id);
    }

    @Operation(summary = "Affecter un employé à une agence",
            description = "400 si l'employé est déjà affecté à une agence : une mutation passe d'abord par la désaffectation.")
    @PatchMapping("/{idEmploye}/agence/{idAgence}")
    public EmployeResponse affecterEmploye(@PathVariable("idEmploye") Long idEmploye,
                                           @PathVariable("idAgence") Long idAgence) {
        return employeService.affecterEmploye(idEmploye, idAgence);
    }

    @Operation(summary = "Désaffecter un employé de son agence",
            description = "Supprime le lien employé -> agence (l'employé et l'agence sont conservés). 400 s'il n'a pas d'agence.")
    @PatchMapping("/{idEmploye}/desaffecter")
    public EmployeResponse desaffecterEmploye(@PathVariable("idEmploye") Long idEmploye) {
        return employeService.desaffecterEmploye(idEmploye);
    }

    @Operation(summary = "Lister les employés par rôle (AGENT ou MANAGER)")
    @GetMapping("/role/{role}")
    public List<EmployeResponse> findByRole(@PathVariable("role") RoleEmploye role) {
        return employeService.findByRole(role);
    }

    @Operation(summary = "Lister les employés d'une agence, filtre optionnel par rôle")
    @GetMapping("/agence/{idAgence}")
    public List<EmployeResponse> findByAgence(@PathVariable("idAgence") Long idAgence,
                                              @RequestParam(value = "role", required = false) RoleEmploye role) {
        return employeService.findByAgence(idAgence, role);
    }
}