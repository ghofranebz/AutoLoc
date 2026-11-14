package tn.esprit.autoloc.service;

/**
 * LSP (cours, slide « EquipeBudgetisable ») : la modification libre est un contrat SÉPARÉ.
 *
 * <p>Problème évité : une interface « CRUD complet » avec {@code update(...)} obligerait
 * PaiementService et ContratService à écrire
 * {@code throw new UnsupportedOperationException("Non applicable")} : tout code qui manipule un
 * « service CRUD » et appelle update() casserait dès qu'on lui passe ces services.</p>
 *
 * <p>Solution : seuls les services qui savent vraiment modifier (agence, client, employé, équipement,
 * véhicule, maintenance) implémentent {@code Modifiable}. Paiement et Contrat ne l'implémentent pas,
 * exactement comme {@code EquipeExterne} n'implémente pas {@code EquipeBudgetisable}.</p>
 *
 * @param <Q>  type de la requête (DTO entrant)
 * @param <R>  type de la réponse (DTO sortant)
 * @param <ID> type de l'identifiant
 */
public interface Modifiable<Q, R, ID> {

    R update(ID id, Q request);
}
