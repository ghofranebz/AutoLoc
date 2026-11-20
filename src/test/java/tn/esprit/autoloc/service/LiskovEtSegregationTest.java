package tn.esprit.autoloc.service;

import org.junit.jupiter.api.Test;
import tn.esprit.autoloc.controller.FacturationController;
import tn.esprit.autoloc.dto.SituationPaiementResponse;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.*;
import tn.esprit.autoloc.repository.*;
import tn.esprit.autoloc.service.impl.*;
import tn.esprit.autoloc.service.support.EntityLoader;
import tn.esprit.autoloc.service.support.JournalisationService;
import tn.esprit.autoloc.service.support.PeriodeValidator;
import tn.esprit.autoloc.service.tarification.TarificationStandard;
import tn.esprit.autoloc.service.validation.ReservationValidator;
import tn.esprit.autoloc.service.validation.VehiculeValidator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * LSP : toutes les implémentations de {@link Consultable} sont substituables les unes aux autres
 * (même comportement pour un id inconnu), et aucun service n'est forcé d'implémenter {@link Modifiable}
 * s'il ne sait pas modifier.
 * ISP : un contrôleur qui n'a besoin que de la facturation fonctionne avec un objet qui n'implémente qu'elle.
 */
class LiskovEtSegregationTest {

    private final EntityLoader loader = new EntityLoader();

    /** Tous les services de l'application, vus à travers la même abstraction Consultable. */
    private List<Consultable<?, Long>> tousLesServices() {
        ReservationRepository reservationRepository = mock(ReservationRepository.class);
        TarificationService tarification = new TarificationService(
                Map.of("standard", new TarificationStandard()), mock(VehiculeRepository.class), loader);
        return List.of(
                new AgenceServiceImpl(mock(AgenceRepository.class), mock(VehiculeRepository.class),
                        mock(EmployeRepository.class), mock(AgenceMapper.class), loader),
                new ClientServiceImpl(mock(ClientRepository.class), reservationRepository,
                        mock(ClientMapper.class), loader),
                new ContratServiceImpl(mock(ContratRepository.class), reservationRepository,
                        mock(ContratMapper.class), loader, tarification, "STANDARD"),
                new EmployeServiceImpl(mock(EmployeRepository.class), mock(AgenceRepository.class),
                        mock(EmployeMapper.class), loader),
                new EquipementServiceImpl(mock(EquipementRepository.class), mock(EquipementMapper.class), loader),
                new MaintenanceServiceImpl(mock(MaintenanceRepository.class), mock(VehiculeRepository.class),
                        mock(MaintenanceMapper.class), loader),
                new PaiementServiceImpl(mock(PaiementRepository.class), mock(ContratRepository.class),
                        mock(PaiementMapper.class), loader),
                new ReservationServiceImpl(reservationRepository, mock(ClientRepository.class),
                        mock(VehiculeRepository.class), mock(ContratRepository.class), mock(ReservationMapper.class),
                        loader, new PeriodeValidator(),
                        new ReservationValidator(reservationRepository, new PeriodeValidator())),
                new VehiculeServiceImpl(mock(VehiculeRepository.class), mock(AgenceRepository.class),
                        mock(VehiculeMapper.class), loader, new PeriodeValidator(),
                        mock(VehiculeValidator.class), new JournalisationService()));
    }

    @Test
    void lsp_chaque_Consultable_respecte_le_meme_contrat_pour_un_id_inconnu() {
        for (Consultable<?, Long> service : tousLesServices()) {
            assertThatThrownBy(() -> service.getById(999L))
                    .as(service.getClass().getSimpleName())
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("introuvable");
            assertThat(service.getAll()).as(service.getClass().getSimpleName()).isEmpty();
        }
    }

    @Test
    void lsp_paiement_et_contrat_ne_promettent_pas_de_modification() {
        // Comme EquipeExterne qui n'implémente pas EquipeBudgetisable dans le cours
        assertThat(Modifiable.class.isAssignableFrom(IPaiementService.class)).isFalse();
        assertThat(Modifiable.class.isAssignableFrom(IContratService.class)).isFalse();
        assertThat(Modifiable.class.isAssignableFrom(IReservationService.class)).isFalse();

        // ... alors que les services qui savent modifier tiennent ce contrat
        assertThat(List.of(IAgenceService.class, IClientService.class, IEmployeService.class,
                IEquipementService.class, IVehiculeService.class, IMaintenanceService.class))
                .allMatch(Modifiable.class::isAssignableFrom);
    }

    @Test
    void isp_le_controleur_de_facturation_ne_depend_que_de_IFacturationService() {
        // IFacturationService n'a qu'une méthode : une lambda suffit. Le contrôleur ignore tout du
        // reste (CRUD des contrats, paiements...), comme ProjetController(ConsultationProjet) dans le cours.
        IFacturationService facturation = id -> new SituationPaiementResponse(
                new BigDecimal("360.00"), new BigDecimal("200.00"), new BigDecimal("160.00"));
        FacturationController controller = new FacturationController(facturation);

        assertThat(controller.situationPaiement(1L).resteAPayer()).isEqualByComparingTo("160.00");
    }
}