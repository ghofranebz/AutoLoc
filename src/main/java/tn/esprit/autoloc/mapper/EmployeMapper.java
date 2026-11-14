package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.dto.EmployeRequest;
import tn.esprit.autoloc.dto.EmployeResponse;

@Component
public class EmployeMapper {

    public Employe toEntity(EmployeRequest r) {
        Employe e = new Employe();
        apply(r, e);
        return e;
    }

    public void apply(EmployeRequest r, Employe e) {
        e.setNom(r.nom());
        e.setPrenom(r.prenom());
        e.setRole(r.role());
    }

    public EmployeResponse toResponse(Employe e) {
        Long idAgence = e.getAgence() != null ? e.getAgence().getIdAgence() : null;
        return new EmployeResponse(e.getIdEmploye(), e.getNom(), e.getPrenom(), e.getRole(), idAgence);
    }
}
