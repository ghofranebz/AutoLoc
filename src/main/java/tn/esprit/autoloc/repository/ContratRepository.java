package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Contrat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContratRepository extends JpaRepository<Contrat, Long> {

    Optional<Contrat> findByReservation_IdReservation(Long idReservation);

    boolean existsByReservation_IdReservation(Long idReservation);

    List<Contrat> findByValide(boolean valide);

    List<Contrat> findByDateSignatureBetween(LocalDate debut, LocalDate fin);

    // Charge le contrat ET ses paiements (collection LAZY) en une seule requête
    @EntityGraph(attributePaths = "paiements")
    Optional<Contrat> findWithPaiementsByIdContrat(Long idContrat);
}
