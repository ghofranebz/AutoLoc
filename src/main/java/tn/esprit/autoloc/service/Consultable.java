package tn.esprit.autoloc.service;

import java.util.List;

/**
 * ISP : contrat minimal de lecture. Un client qui ne fait que consulter ne dépend de rien d'autre.
 *
 * @param <R> type de la réponse (DTO)
 * @param <I> type de l'identifiant
 */
public interface Consultable<R, I> {

    List<R> getAll();

    R getById(I id);
}