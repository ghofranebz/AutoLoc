package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Equipement;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EquipementRepository extends JpaRepository<Equipement, Long> {

    Optional<Equipement> findByLibelle(String libelle);

    boolean existsByLibelle(String libelle);

    List<Equipement> findByLibelleIn(Collection<String> libelles);
}
