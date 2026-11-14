package tn.esprit.autoloc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.VehiculeRequest;
import tn.esprit.autoloc.dto.VehiculeResponse;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.VehiculeMapper;
import tn.esprit.autoloc.repository.AgenceRepository;
import tn.esprit.autoloc.repository.VehiculeRepository;
import tn.esprit.autoloc.service.IVehiculeService;
import tn.esprit.autoloc.service.support.EntityLoader;
import tn.esprit.autoloc.service.support.JournalisationService;
import tn.esprit.autoloc.service.support.PeriodeValidator;
import tn.esprit.autoloc.service.validation.VehiculeValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Atelier GoF/SOLID, partie B (SRP + DIP) : opérations métier sur les véhicules uniquement.
 * Validation -> VehiculeValidator ; journalisation -> JournalisationService ; prix -> TarificationService.
 * DIP : dépend de l'interface VehiculeRepository, injectée par Spring via le constructeur (@RequiredArgsConstructor).
 */
@Service
@Transactional
@RequiredArgsConstructor
public class VehiculeServiceImpl implements IVehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final AgenceRepository agenceRepository;
    private final VehiculeMapper mapper;
    private final EntityLoader loader;
    private final PeriodeValidator periodeValidator;
    private final VehiculeValidator vehiculeValidator;
    private final JournalisationService journalisation;

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResponse> getAll() {
        return vehiculeRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculeResponse getById(Long id) {
        return mapper.toResponse(charger(id));
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculeResponse getByImmatriculation(String immatriculation) {
        return vehiculeRepository.findByImmatriculation(immatriculation)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun véhicule immatriculé " + immatriculation));
    }

    @Override
    public VehiculeResponse add(VehiculeRequest request) {
        vehiculeValidator.validerAjout(request);                      // validation   -> VehiculeValidator
        Vehicule vehicule = mapper.toEntity(request);
        if (vehicule.getStatut() == null) {
            vehicule.setStatut(StatutVehicule.DISPONIBLE);
        }
        if (request.idAgence() != null) {
            vehicule.setAgence(loader.charger(agenceRepository, request.idAgence(), "Agence"));
        }
        Vehicule enregistre = vehiculeRepository.save(vehicule);      // persistance  -> VehiculeRepository
        journalisation.operation("Ajout", "véhicule", enregistre.getIdVehicule()); // journalisation
        return mapper.toResponse(enregistre);
    }

    @Override
    public VehiculeResponse update(Long id, VehiculeRequest request) {
        Vehicule existant = charger(id);
        vehiculeValidator.validerModification(existant, request);
        mapper.apply(request, existant);
        Vehicule enregistre = vehiculeRepository.save(existant);
        journalisation.operation("Modification", "véhicule", id);
        return mapper.toResponse(enregistre);
    }

    @Override
    public void delete(Long id) {
        Vehicule vehicule = charger(id);
        vehiculeValidator.validerSuppression(id);
        vehicule.getEquipements().clear();
        vehiculeRepository.delete(vehicule);
        journalisation.operation("Suppression", "véhicule", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResponse> rechercher(StatutVehicule statut, CategorieVehicule categorie) {
        List<Vehicule> resultat;
        if (statut != null && categorie != null) {
            resultat = vehiculeRepository.findByStatutAndCategorie(statut, categorie);
        } else if (statut != null) {
            resultat = vehiculeRepository.findByStatut(statut);
        } else if (categorie != null) {
            resultat = vehiculeRepository.findByCategorie(categorie);
        } else {
            resultat = vehiculeRepository.findAll();
        }
        return resultat.stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResponse> findByAgence(Long idAgence) {
        loader.charger(agenceRepository, idAgence, "Agence");
        return vehiculeRepository.findByAgence_IdAgence(idAgence).stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResponse> findByTarif(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) > 0) {
            throw new BusinessRuleException("Le tarif min doit être inférieur ou égal au tarif max");
        }
        return vehiculeRepository.findByTarifJournalierBetween(min, max).stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResponse> findDisponibles(LocalDate dateDebut, LocalDate dateFin) {
        periodeValidator.verifier(dateDebut, dateFin);
        return vehiculeRepository.findDisponiblesEntre(dateDebut, dateFin).stream().map(mapper::toResponse).toList();
    }

    private Vehicule charger(Long id) {
        return vehiculeRepository.findWithEquipementsByIdVehicule(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule introuvable avec l'id " + id));
    }
}