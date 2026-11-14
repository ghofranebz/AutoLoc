package tn.esprit.autoloc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.dto.SituationPaiementResponse;
import tn.esprit.autoloc.repository.ContratRepository;
import tn.esprit.autoloc.repository.PaiementRepository;
import tn.esprit.autoloc.service.IFacturationService;
import tn.esprit.autoloc.service.support.EntityLoader;

import java.math.BigDecimal;

/**
 * SRP : facturation d'un contrat (Contrat.montantTotal vs paiements du contrat).
 * Sortie de ContratServiceImpl, qui ne garde que le cycle de vie du contrat et ne dépend plus de PaiementRepository.
 * DIP : dépend uniquement d'abstractions (interfaces de repository) injectées par le constructeur.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FacturationServiceImpl implements IFacturationService {

    private final ContratRepository contratRepository;
    private final PaiementRepository paiementRepository;
    private final EntityLoader loader;

    @Override
    public SituationPaiementResponse situationPaiement(Long idContrat) {
        Contrat contrat = loader.charger(contratRepository, idContrat, "Contrat");
        BigDecimal totalPaye = paiementRepository.totalPayeParContrat(idContrat);
        return new SituationPaiementResponse(contrat.getMontantTotal(), totalPaye,
                contrat.getMontantTotal().subtract(totalPaye));
    }
}
