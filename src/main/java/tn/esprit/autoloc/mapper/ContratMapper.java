package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.dto.ContratResponse;

@Component
public class ContratMapper {

    public ContratResponse toResponse(Contrat c) {
        Long idReservation = c.getReservation() != null ? c.getReservation().getIdReservation() : null;
        return new ContratResponse(c.getIdContrat(), c.getDateSignature(), c.getMontantTotal(), c.isValide(),
                idReservation);
    }
}
