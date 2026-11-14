package tn.esprit.autoloc.service;

import tn.esprit.autoloc.dto.ContratResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Cycle de vie d'un contrat.
 * LSP : un contrat n'est pas modifiable librement, donc cette interface n'étend PAS {@link Modifiable}
 * (aucune méthode update qui lèverait UnsupportedOperationException).
 * ISP : la facturation (situation de paiement) est dans {@link IFacturationService}.
 */
public interface IContratService extends Consultable<ContratResponse, Long>, Supprimable<Long> {

    ContratResponse creer(Long idReservation);

    ContratResponse changerValidite(Long id, boolean valide);

    ContratResponse getByReservation(Long idReservation);

    List<ContratResponse> findByValide(boolean valide);

    List<ContratResponse> findSigneEntre(LocalDate debut, LocalDate fin);
}
