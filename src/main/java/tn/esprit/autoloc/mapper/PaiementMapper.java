package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.dto.PaiementRequest;
import tn.esprit.autoloc.dto.PaiementResponse;

@Component
public class PaiementMapper {

    public Paiement toEntity(PaiementRequest r) {
        Paiement p = new Paiement();
        p.setMontant(r.montant());
        p.setDatePaiement(r.datePaiement());
        p.setModePaiement(r.modePaiement());
        return p;
    }

    public PaiementResponse toResponse(Paiement p) {
        Long idContrat = p.getContrat() != null ? p.getContrat().getIdContrat() : null;
        return new PaiementResponse(p.getIdPaiement(), p.getMontant(), p.getDatePaiement(), p.getModePaiement(),
                idContrat);
    }
}
