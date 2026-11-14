package tn.esprit.autoloc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.RoleEmploye;
import tn.esprit.autoloc.dto.EmployeRequest;
import tn.esprit.autoloc.dto.EmployeResponse;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.mapper.EmployeMapper;
import tn.esprit.autoloc.repository.AgenceRepository;
import tn.esprit.autoloc.repository.EmployeRepository;
import tn.esprit.autoloc.service.IEmployeService;
import tn.esprit.autoloc.service.support.EntityLoader;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeServiceImpl implements IEmployeService {

    private final EmployeRepository employeRepository;
    private final AgenceRepository agenceRepository;
    private final EmployeMapper mapper;
    private final EntityLoader loader;

    @Override
    @Transactional(readOnly = true)
    public List<EmployeResponse> getAll() {
        return employeRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeResponse getById(Long id) {
        return mapper.toResponse(charger(id));
    }

    @Override
    public EmployeResponse add(EmployeRequest request) {
        Employe employe = mapper.toEntity(request);
        if (request.idAgence() != null) {
            employe.setAgence(loader.charger(agenceRepository, request.idAgence(), "Agence"));
        }
        return mapper.toResponse(employeRepository.save(employe));
    }

    @Override
    public EmployeResponse update(Long id, EmployeRequest request) {
        Employe existant = charger(id);
        mapper.apply(request, existant);
        return mapper.toResponse(employeRepository.save(existant));
    }

    @Override
    public void delete(Long id) {
        employeRepository.delete(charger(id));
    }

    /**
     * Scénario 1 (@ManyToOne Employe -> Agence) : affecter un employé à une agence.
     * Employe est le côté PROPRIÉTAIRE (il porte la clé étrangère id_agence) : l'affectation se fait
     * donc sur l'employé avec setAgence(...). Un employé n'appartient qu'à une seule agence à la fois :
     * s'il en a déjà une, le transfert doit d'abord passer par desaffecterEmploye.
     */
    @Override
    public EmployeResponse affecterEmploye(Long idEmploye, Long idAgence) {
        Employe employe = charger(idEmploye);                                    // 1. récupérer l'employé
        Agence agence = loader.charger(agenceRepository, idAgence, "Agence");    // 2. récupérer l'agence
        if (employe.getAgence() != null) {
            throw new BusinessRuleException("L'employé " + idEmploye + " est déjà affecté à l'agence "
                    + employe.getAgence().getIdAgence() + " : désaffectez-le d'abord");
        }
        employe.setAgence(agence);                                               // 3. lier (côté propriétaire)
        return mapper.toResponse(employeRepository.save(employe));               // 4. sauvegarder
    }

    /**
     * Scénario 1 : désaffecter un employé de son agence (première étape d'une mutation).
     * On met la référence à null côté propriétaire : le lien est supprimé, ni l'employé ni l'agence
     * ne sont supprimés.
     */
    @Override
    public EmployeResponse desaffecterEmploye(Long idEmploye) {
        Employe employe = charger(idEmploye);
        if (employe.getAgence() == null) {
            throw new BusinessRuleException("L'employé " + idEmploye + " n'est affecté à aucune agence");
        }
        employe.setAgence(null);
        return mapper.toResponse(employeRepository.save(employe));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeResponse> findByRole(RoleEmploye role) {
        return employeRepository.findByRole(role).stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeResponse> findByAgence(Long idAgence, RoleEmploye role) {
        List<Employe> employes = role == null
                ? employeRepository.findByAgence_IdAgence(idAgence)
                : employeRepository.findByAgence_IdAgenceAndRole(idAgence, role);
        return employes.stream().map(mapper::toResponse).toList();
    }

    private Employe charger(Long id) {
        return loader.charger(employeRepository, id, "Employe");
    }
}