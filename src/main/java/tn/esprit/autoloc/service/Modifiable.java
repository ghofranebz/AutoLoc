package tn.esprit.autoloc.service;

/**
 * LSP (cours, slide « EquipeBudgetisable ») : la modification libre est un contrat SÉPARÉ.
 * Seuls les services qui savent vraiment modifier (agence, client, employé, équipement,
 * véhicule, maintenance) implémentent Modifiable. Paiement et Contrat ne l'implémentent pas.
 *
 * @param <Q> type de la requête (DTO entrant)
 * @param <R> type de la réponse (DTO sortant)
 * @param <I> type de l'identifiant
 */
public interface Modifiable<Q, R, I> {

    R update(I id, Q request);
}