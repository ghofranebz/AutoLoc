package tn.esprit.autoloc.service;

import tn.esprit.autoloc.dto.SituationPaiementResponse;

/**
 * ISP (cours, slide « FacturationProjet ») : la facturation d'un contrat est un contrat à part.
 * Un client qui gère le cycle de vie des contrats (IContratService) ne dépend pas de la facturation,
 * et FacturationController ne dépend que de cette interface.
 */
public interface IFacturationService {

    /** Montant total du contrat, somme de ses paiements et reste à payer. */
    SituationPaiementResponse situationPaiement(Long idContrat);
}
