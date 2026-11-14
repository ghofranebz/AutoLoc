package tn.esprit.autoloc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.dto.EquipementRequest;
import tn.esprit.autoloc.dto.EquipementResponse;
import tn.esprit.autoloc.exception.ConflictException;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.EquipementMapper;
import tn.esprit.autoloc.repository.EquipementRepository;
import tn.esprit.autoloc.service.IEquipementService;
import tn.esprit.autoloc.service.support.EntityLoader;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EquipementServiceImpl implements IEquipementService {

    private final EquipementRepository equipementRepository;
    private final EquipementMapper mapper;
    private final EntityLoader loader;

    @Override
    @Transactional(readOnly = true)
    public List<EquipementResponse> getAll() {
        return equipementRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EquipementResponse getById(Long id) {
        return mapper.toResponse(charger(id));
    }

    @Override
    public EquipementResponse add(EquipementRequest request) {
        verifierLibelleLibre(request.libelle());
        return mapper.toResponse(equipementRepository.save(mapper.toEntity(request)));
    }

    @Override
    public EquipementResponse update(Long id, EquipementRequest request) {
        Equipement existant = charger(id);
        if (!existant.getLibelle().equals(request.libelle())) {
            verifierLibelleLibre(request.libelle());
        }
        existant.setLibelle(request.libelle());
        return mapper.toResponse(equipementRepository.save(existant));
    }

    @Override
    public void delete(Long id) {
        Equipement equipement = charger(id);
        // Vehicule est le côté propriétaire du ManyToMany : on détache l'équipement de chaque véhicule
        equipement.getVehicules().forEach(v -> v.getEquipements().remove(equipement));
        equipementRepository.delete(equipement);
    }

    @Override
    @Transactional(readOnly = true)
    public EquipementResponse getByLibelle(String libelle) {
        return equipementRepository.findByLibelle(libelle)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun équipement avec le libellé " + libelle));
    }

    private void verifierLibelleLibre(String libelle) {
        if (equipementRepository.existsByLibelle(libelle)) {
            throw new ConflictException("L'équipement '" + libelle + "' existe déjà");
        }
    }

    private Equipement charger(Long id) {
        return loader.charger(equipementRepository, id, "Equipement");
    }
}
