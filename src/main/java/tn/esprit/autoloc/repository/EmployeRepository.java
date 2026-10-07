package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.RoleEmploye;

import java.util.List;

public interface EmployeRepository extends JpaRepository<Employe, Long> {

    List<Employe> findByRole(RoleEmploye role);

    List<Employe> findByAgence_IdAgence(Long idAgence);

    List<Employe> findByAgence_IdAgenceAndRole(Long idAgence, RoleEmploye role);
}
