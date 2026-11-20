package tn.esprit.autoloc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.dto.ClientRequest;
import tn.esprit.autoloc.dto.ClientResponse;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.exception.ConflictException;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.ClientMapper;
import tn.esprit.autoloc.repository.ClientRepository;
import tn.esprit.autoloc.repository.ReservationRepository;
import tn.esprit.autoloc.service.IClientService;
import tn.esprit.autoloc.service.support.EntityLoader;
import tn.esprit.autoloc.service.support.Fuseau;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService {

    private final ClientRepository clientRepository;
    private final ReservationRepository reservationRepository;
    private final ClientMapper mapper;
    private final EntityLoader loader;

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponse> getAll() {
        return clientRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getById(Long id) {
        return mapper.toResponse(charger(id));
    }

    @Override
    public ClientResponse add(ClientRequest request) {
        if (clientRepository.existsByEmail(request.email())) {
            throw new ConflictException("Un client avec l'email " + request.email() + " existe déjà");
        }
        if (clientRepository.existsByNumPermis(request.numPermis())) {
            throw new ConflictException("Un client avec le permis " + request.numPermis() + " existe déjà");
        }
        Client client = mapper.toEntity(request);
        if (client.getDateInscription() == null) {
            client.setDateInscription(LocalDate.now(Fuseau.TUNIS));
        }
        return mapper.toResponse(clientRepository.save(client));
    }

    @Override
    public ClientResponse update(Long id, ClientRequest request) {
        Client existant = charger(id);
        if (!request.email().equals(existant.getEmail()) && clientRepository.existsByEmail(request.email())) {
            throw new ConflictException("L'email " + request.email() + " est déjà utilisé");
        }
        if (!request.numPermis().equals(existant.getNumPermis())
                && clientRepository.existsByNumPermis(request.numPermis())) {
            throw new ConflictException("Le permis " + request.numPermis() + " est déjà utilisé");
        }
        mapper.apply(request, existant);
        return mapper.toResponse(clientRepository.save(existant));
    }

    @Override
    public void delete(Long id) {
        Client client = charger(id);
        if (!reservationRepository.findByClient_IdClient(id).isEmpty()) {
            throw new BusinessRuleException("Impossible de supprimer le client " + id + " : il a des réservations");
        }
        clientRepository.delete(client);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getByEmail(String email) {
        return clientRepository.findByEmail(email)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun client avec l'email " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getByNumPermis(String numPermis) {
        return clientRepository.findByNumPermis(numPermis)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun client avec le permis " + numPermis));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponse> search(String motCle) {
        return clientRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(motCle, motCle)
                .stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponse> inscritsApres(LocalDate date) {
        return clientRepository.findByDateInscriptionAfter(date).stream().map(mapper::toResponse).toList();
    }

    private Client charger(Long id) {
        return loader.charger(clientRepository, id, "Client");
    }
}