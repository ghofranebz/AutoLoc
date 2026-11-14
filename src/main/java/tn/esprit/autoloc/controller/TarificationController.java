package tn.esprit.autoloc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.autoloc.dto.TarifResponse;
import tn.esprit.autoloc.service.ITarificationService;

import java.util.Set;

/** Expose le calcul de prix (patron Strategy) pour le tester dans Swagger. */
@Tag(name = "Tarification", description = "Calcul du prix d'une location selon une stratégie (patron Strategy)")
@RestController
@RequestMapping("/api/tarification")
@RequiredArgsConstructor
public class TarificationController {

    private final ITarificationService tarificationService;

    @Operation(summary = "Lister les types de tarif disponibles")
    @GetMapping("/types")
    public Set<String> types() {
        return tarificationService.typesDisponibles();
    }

    @Operation(summary = "Calculer le prix de location d'un véhicule",
            description = "400 si le type est inconnu ou si jours <= 0, 404 si le véhicule n'existe pas.")
    @GetMapping("/vehicules/{idVehicule}")
    public TarifResponse calculer(
            @PathVariable("idVehicule") Long idVehicule,
            @Parameter(description = "STANDARD, WEEKEND, LONGUE_DUREE ou ENTREPRISE", example = "WEEKEND")
            @RequestParam(value = "type", defaultValue = "STANDARD") String type,
            @Parameter(description = "Nombre de jours de location", example = "5")
            @RequestParam("jours") int jours) {
        return tarificationService.calculerPourVehicule(idVehicule, type, jours);
    }
}