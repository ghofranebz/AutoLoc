package tn.esprit.autoloc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.ModePaiement;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.dto.PaiementRequest;
import tn.esprit.autoloc.dto.PaiementResponse;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.mapper.PaiementMapper;
import tn.esprit.autoloc.repository.ContratRepository;
import tn.esprit.autoloc.repository.PaiementRepository;
import tn.esprit.autoloc.service.IPaiementService;
import tn.esprit.autoloc.service.support.EntityLoader;
import tn.esprit.autoloc.service.support.Fuseau;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PaiementServiceImpl implements IPaiementService {

    private final PaiementRepository paiementRepository;
    private final ContratRepository contratRepository;
    private final PaiementMapper mapper;
    private final EntityLoader loader;

    @Override
    @Transactional(readOnly = true)
    public List<PaiementResponse> getAll() {
        return paiementRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaiementResponse getById(Long id) {
        return mapper.toResponse(charger(id));
    }

    @Override
    public PaiementResponse ajouter(Long idContrat, PaiementRequest request) {
        Contrat contrat = loader.charger(contratRepository, idContrat, "Contrat");
        if (!contrat.isValide()) {
            throw new BusinessRuleException("Le contrat " + idContrat + " n'est pas valide");
        }
        BigDecimal reste = contrat.getMontantTotal().subtract(paiementRepository.totalPayeParContrat(idContrat));
        if (request.montant().compareTo(reste) > 0) {
            throw new BusinessRuleException("Le montant (" + request.montant()
                    + ") dépasse le reste à payer (" + reste + ")");
        }
        Paiement paiement = mapper.toEntity(request);
        if (paiement.getDatePaiement() == null) {
            paiement.setDatePaiement(LocalDate.now(Fuseau.TUNIS));
        }
        paiement.setContrat(contrat);
        return mapper.toResponse(paiementRepository.save(paiement));
    }

    @Override
    public void delete(Long id) {
        Paiement paiement = charger(id);
        // retirer de la collection du contrat (orphanRemoval) pour éviter une ré-insertion
        paiement.getContrat().getPaiements().remove(paiement);
        paiementRepository.delete(paiement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementResponse> findByContrat(Long idContrat) {
        return paiementRepository.findByContrat_IdContrat(idContrat).stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementResponse> findByMode(ModePaiement mode) {
        return paiementRepository.findByModePaiement(mode).stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementResponse> findEntre(LocalDate debut, LocalDate fin) {
        return paiementRepository.findByDatePaiementBetween(debut, fin).stream().map(mapper::toResponse).toList();
    }

    private Paiement charger(Long id) {
        return loader.charger(paiementRepository, id, "Paiement");
    }
}
