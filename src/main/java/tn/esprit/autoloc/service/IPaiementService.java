package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.ModePaiement;
import tn.esprit.autoloc.dto.PaiementRequest;
import tn.esprit.autoloc.dto.PaiementResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * LSP : un paiement s'ajoute ou se supprime mais ne se modifie pas. Cette interface n'étend donc PAS
 * {@link Modifiable} : aucune implémentation n'est forcée d'écrire un update() qui lèverait
 * UnsupportedOperationException (le contre-exemple « EquipeExterne » du cours).
 */
public interface IPaiementService extends Consultable<PaiementResponse, Long>, Supprimable<Long> {

    PaiementResponse ajouter(Long idContrat, PaiementRequest request);

    List<PaiementResponse> findByContrat(Long idContrat);

    List<PaiementResponse> findByMode(ModePaiement mode);

    List<PaiementResponse> findEntre(LocalDate debut, LocalDate fin);
}
