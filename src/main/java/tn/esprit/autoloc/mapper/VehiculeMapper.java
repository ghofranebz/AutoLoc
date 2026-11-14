package tn.esprit.autoloc.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.EquipementResponse;
import tn.esprit.autoloc.dto.VehiculeRequest;
import tn.esprit.autoloc.dto.VehiculeResponse;

import java.util.List;

/** Injection de dépendances entre beans : le mapper de véhicule réutilise le mapper d'équipement. */
@Component
@RequiredArgsConstructor
public class VehiculeMapper {

    private final EquipementMapper equipementMapper;

    public Vehicule toEntity(VehiculeRequest r) {
        Vehicule v = new Vehicule();
        apply(r, v);
        v.setStatut(r.statut());
        return v;
    }

    /** Copie les champs modifiables ; le statut n'est remplacé que s'il est fourni. */
    public void apply(VehiculeRequest r, Vehicule v) {
        v.setImmatriculation(r.immatriculation());
        v.setMarque(r.marque());
        v.setModele(r.modele());
        v.setCategorie(r.categorie());
        v.setTarifJournalier(r.tarifJournalier());
        if (r.statut() != null) {
            v.setStatut(r.statut());
        }
    }

    public VehiculeResponse toResponse(Vehicule v) {
        Long idAgence = v.getAgence() != null ? v.getAgence().getIdAgence() : null;
        List<EquipementResponse> equipements = v.getEquipements().stream()
                .map(equipementMapper::toResponse)
                .toList();
        return new VehiculeResponse(v.getIdVehicule(), v.getImmatriculation(), v.getMarque(), v.getModele(),
                v.getCategorie(), v.getTarifJournalier(), v.getStatut(), idAgence, equipements);
    }
}
