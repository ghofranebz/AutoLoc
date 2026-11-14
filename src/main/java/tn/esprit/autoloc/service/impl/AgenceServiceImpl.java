package tn.esprit.autoloc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.dto.AgenceRequest;
import tn.esprit.autoloc.dto.AgenceResponse;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.mapper.AgenceMapper;
import tn.esprit.autoloc.repository.AgenceRepository;
import tn.esprit.autoloc.repository.EmployeRepository;
import tn.esprit.autoloc.repository.VehiculeRepository;
import tn.esprit.autoloc.service.IAgenceService;
import tn.esprit.autoloc.service.support.EntityLoader;

import java.util.List;

/**
 * Bean Spring (@Service, singleton). Toutes les dépendances sont injectées par le constructeur
 * généré par Lombok : elles sont finales, obligatoires et remplaçables par des mocks dans les tests.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class AgenceServiceImpl implements IAgenceService {

    private final AgenceRepository agenceRepository;
    private final VehiculeRepository vehiculeRepository;
    private final EmployeRepository employeRepository;
    private final AgenceMapper mapper;
    private final EntityLoader loader;

    @Override
    @Transactional(readOnly = true)
    public List<AgenceResponse> getAll() {
        return agenceRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AgenceResponse getById(Long id) {
        return mapper.toResponse(charger(id));
    }

    @Override
    public AgenceResponse add(AgenceRequest request) {
        return mapper.toResponse(agenceRepository.save(mapper.toEntity(request)));
    }

    @Override
    public AgenceResponse update(Long id, AgenceRequest request) {
        Agence existante = charger(id);
        mapper.apply(request, existante);
        return mapper.toResponse(agenceRepository.save(existante));
    }

    @Override
    public void delete(Long id) {
        Agence agence = charger(id);
        if (!vehiculeRepository.findByAgence_IdAgence(id).isEmpty()
                || !employeRepository.findByAgence_IdAgence(id).isEmpty()) {
            throw new BusinessRuleException("Impossible de supprimer l'agence " + id
                    + " : elle possède encore des véhicules ou des employés");
        }
        agenceRepository.delete(agence);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgenceResponse> findByVille(String ville) {
        return agenceRepository.findByVilleIgnoreCase(ville).stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgenceResponse> searchByNom(String nom) {
        return agenceRepository.findByNomContainingIgnoreCase(nom).stream().map(mapper::toResponse).toList();
    }

    private Agence charger(Long id) {
        return loader.charger(agenceRepository, id, "Agence");
    }
}
