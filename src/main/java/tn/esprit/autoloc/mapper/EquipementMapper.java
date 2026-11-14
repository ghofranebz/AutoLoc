package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.dto.EquipementRequest;
import tn.esprit.autoloc.dto.EquipementResponse;

@Component
public class EquipementMapper {

    public Equipement toEntity(EquipementRequest r) {
        Equipement e = new Equipement();
        e.setLibelle(r.libelle());
        return e;
    }

    public EquipementResponse toResponse(Equipement e) {
        return new EquipementResponse(e.getIdEquipement(), e.getLibelle());
    }
}
