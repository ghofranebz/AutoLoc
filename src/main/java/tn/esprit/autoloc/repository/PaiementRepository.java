package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.autoloc.domain.ModePaiement;
import tn.esprit.autoloc.domain.Paiement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    List<Paiement> findByContrat_IdContrat(Long idContrat);

    List<Paiement> findByModePaiement(ModePaiement modePaiement);

    List<Paiement> findByDatePaiementBetween(LocalDate debut, LocalDate fin);

    // Total déjà payé pour un contrat (0 s'il n'y a aucun paiement)
    @Query("select coalesce(sum(p.montant), 0) from Paiement p where p.contrat.idContrat = :idContrat")
    BigDecimal totalPayeParContrat(@Param("idContrat") Long idContrat);
}
