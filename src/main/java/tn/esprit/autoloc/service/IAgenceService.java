package tn.esprit.autoloc.service;

import tn.esprit.autoloc.dto.AgenceRequest;
import tn.esprit.autoloc.dto.AgenceResponse;

import java.util.List;

public interface IAgenceService extends Consultable<AgenceResponse, Long>,
        Modifiable<AgenceRequest, AgenceResponse, Long>, Supprimable<Long> {

    AgenceResponse add(AgenceRequest request);

    List<AgenceResponse> findByVille(String ville);

    List<AgenceResponse> searchByNom(String nom);
}
