package tn.esprit.autoloc.service;

import tn.esprit.autoloc.dto.ClientRequest;
import tn.esprit.autoloc.dto.ClientResponse;

import java.time.LocalDate;
import java.util.List;

public interface IClientService extends Consultable<ClientResponse, Long>,
        Modifiable<ClientRequest, ClientResponse, Long>, Supprimable<Long> {

    ClientResponse add(ClientRequest request);

    ClientResponse getByEmail(String email);

    ClientResponse getByNumPermis(String numPermis);

    List<ClientResponse> search(String motCle);

    List<ClientResponse> inscritsApres(LocalDate date);
}
