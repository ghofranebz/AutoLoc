package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.RoleEmploye;
import tn.esprit.autoloc.dto.EmployeRequest;
import tn.esprit.autoloc.dto.EmployeResponse;

import java.util.List;

public interface IEmployeService extends Consultable<EmployeResponse, Long>,
        Modifiable<EmployeRequest, EmployeResponse, Long>, Supprimable<Long> {

    EmployeResponse add(EmployeRequest request);

    /** Affectation @ManyToOne : rattache un employé SANS agence à une agence. */
    EmployeResponse affecterEmploye(Long idEmploye, Long idAgence);

    /** Désaffectation @ManyToOne : retire l'employé de son agence (l'employé n'est pas supprimé). */
    EmployeResponse desaffecterEmploye(Long idEmploye);

    List<EmployeResponse> findByRole(RoleEmploye role);

    List<EmployeResponse> findByAgence(Long idAgence, RoleEmploye role);
}