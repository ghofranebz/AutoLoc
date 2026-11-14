package tn.esprit.autoloc.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.dto.ClientRequest;
import tn.esprit.autoloc.dto.ClientResponse;

@Component
public class ClientMapper {

    public Client toEntity(ClientRequest r) {
        Client c = new Client();
        apply(r, c);
        c.setDateInscription(r.dateInscription());
        return c;
    }

    /** Copie les champs modifiables (la date d'inscription n'est jamais modifiée). */
    public void apply(ClientRequest r, Client c) {
        c.setNom(r.nom());
        c.setPrenom(r.prenom());
        c.setEmail(r.email());
        c.setTelephone(r.telephone());
        c.setNumPermis(r.numPermis());
    }

    public ClientResponse toResponse(Client c) {
        return new ClientResponse(c.getIdClient(), c.getNom(), c.getPrenom(), c.getEmail(), c.getTelephone(),
                c.getNumPermis(), c.getDateInscription());
    }
}
