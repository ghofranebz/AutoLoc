package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.dto.AgenceRequest;
import tn.esprit.autoloc.dto.AgenceResponse;

/** SRP : conversion entité <-> DTO, aucune logique métier. Bean Spring injecté dans les services. */
@Component
public class AgenceMapper {

    public Agence toEntity(AgenceRequest r) {
        Agence a = new Agence();
        apply(r, a);
        return a;
    }

    public void apply(AgenceRequest r, Agence a) {
        a.setNom(r.nom());
        a.setVille(r.ville());
        a.setAdresse(r.adresse());
        a.setTelephone(r.telephone());
    }

    public AgenceResponse toResponse(Agence a) {
        return new AgenceResponse(a.getIdAgence(), a.getNom(), a.getVille(), a.getAdresse(), a.getTelephone());
    }
}
