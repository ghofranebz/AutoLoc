package tn.esprit.autoloc.service;

import tn.esprit.autoloc.dto.EquipementRequest;
import tn.esprit.autoloc.dto.EquipementResponse;

public interface IEquipementService extends Consultable<EquipementResponse, Long>,
        Modifiable<EquipementRequest, EquipementResponse, Long>, Supprimable<Long> {

    EquipementResponse add(EquipementRequest request);

    EquipementResponse getByLibelle(String libelle);
}
