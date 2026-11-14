package tn.esprit.autoloc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.RoleEmploye;
import tn.esprit.autoloc.dto.EmployeRequest;
import tn.esprit.autoloc.dto.EmployeResponse;
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

    @Override
    public EmployeResponse affecterAgence(Long idEmploye, Long idAgence) {
        Employe employe = charger(idEmploye);
        employe.setAgence(loader.charger(agenceRepository, idAgence, "Agence"));
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
