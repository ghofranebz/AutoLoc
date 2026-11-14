package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.RoleEmploye;
import tn.esprit.autoloc.dto.EmployeRequest;
import tn.esprit.autoloc.dto.EmployeResponse;

import java.util.List;

public interface IEmployeService extends Consultable<EmployeResponse, Long>,
        Modifiable<EmployeRequest, EmployeResponse, Long>, Supprimable<Long> {

    EmployeResponse add(EmployeRequest request);

    EmployeResponse affecterAgence(Long idEmploye, Long idAgence);

    List<EmployeResponse> findByRole(RoleEmploye role);

    List<EmployeResponse> findByAgence(Long idAgence, RoleEmploye role);
}
