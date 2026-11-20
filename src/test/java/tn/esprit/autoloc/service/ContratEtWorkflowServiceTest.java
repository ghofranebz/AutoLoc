package tn.esprit.autoloc.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.ContratResponse;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.mapper.ContratMapper;
import tn.esprit.autoloc.mapper.ReservationMapper;
import tn.esprit.autoloc.repository.ContratRepository;
import tn.esprit.autoloc.repository.ReservationRepository;
import tn.esprit.autoloc.repository.VehiculeRepository;
import tn.esprit.autoloc.service.impl.ContratServiceImpl;
import tn.esprit.autoloc.service.impl.ReservationWorkflowServiceImpl;
import tn.esprit.autoloc.service.impl.TarificationService;
import tn.esprit.autoloc.service.support.EntityLoader;
import tn.esprit.autoloc.service.tarification.TarificationLongueDuree;
import tn.esprit.autoloc.service.tarification.TarificationStandard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContratEtWorkflowServiceTest {

    @Mock ContratRepository contratRepository;
    @Mock ReservationRepository reservationRepository;

    private final TarificationService tarification = new TarificationService(
            Map.of("standard", new TarificationStandard(), "longueDuree", new TarificationLongueDuree()),
            mock(VehiculeRepository.class), new EntityLoader());

    private ContratServiceImpl contratService(String typeTarification) {
        return new ContratServiceImpl(contratRepository, reservationRepository,
                new ContratMapper(), new EntityLoader(), tarification, typeTarification);
    }

    private Reservation reservationConfirmee(int jours) {
        Vehicule v = new Vehicule();
        v.setIdVehicule(2L);
        v.setStatut(StatutVehicule.DISPONIBLE);
        v.setTarifJournalier(new BigDecimal("90.00"));
        Reservation r = new Reservation();
        r.setIdReservation(5L);
        r.setStatut(StatutReservation.CONFIRMEE);
        r.setDateDebut(LocalDate.of(2026, 11, 1));
        r.setDateFin(LocalDate.of(2026, 11, 1).plusDays(jours));
        r.setVehicule(v);
        return r;
    }

    @Test
    void creerContrat_calcule_le_montant_standard_et_loue_le_vehicule() {
        Reservation r = reservationConfirmee(4);
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(r));
        when(contratRepository.existsByReservation_IdReservation(5L)).thenReturn(false);
        when(contratRepository.save(any(Contrat.class))).thenAnswer(i -> i.getArgument(0));

        ContratResponse c = contratService("STANDARD").creer(5L);

        assertThat(c.montantTotal()).isEqualByComparingTo("360.00");
        assertThat(c.valide()).isTrue();
        assertThat(c.idReservation()).isEqualTo(5L);
        assertThat(r.getVehicule().getStatut()).isEqualTo(StatutVehicule.LOUE);
    }

    @Test
    void changer_de_strategie_ne_modifie_pas_le_service() {
        Reservation r = reservationConfirmee(10);
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(r));
        when(contratRepository.existsByReservation_IdReservation(5L)).thenReturn(false);
        when(contratRepository.save(any(Contrat.class))).thenAnswer(i -> i.getArgument(0));

        ContratResponse c = contratService("LONGUE_DUREE").creer(5L);

        assertThat(c.montantTotal()).isEqualByComparingTo("720.00"); // 10 x 90 - 20 %
    }

    @Test
    void strategie_inconnue_est_refusee_au_demarrage() {
        assertThatThrownBy(() -> contratService("inexistante"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("inexistante");
    }

    @Test
    void creerContrat_exige_une_reservation_confirmee() {
        Reservation r = reservationConfirmee(2);
        r.setStatut(StatutReservation.EN_ATTENTE);
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(r));

        ContratServiceImpl service = contratService("STANDARD");
        assertThatThrownBy(() -> service.creer(5L))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void workflow_confirmer_puis_refuser_une_deuxieme_confirmation() {
        ReservationWorkflowServiceImpl workflow = new ReservationWorkflowServiceImpl(
                reservationRepository, contratRepository, new ReservationMapper(), new EntityLoader());
        Reservation r = reservationConfirmee(2);
        r.setStatut(StatutReservation.EN_ATTENTE);
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(r));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        assertThat(workflow.confirmer(5L).statut()).isEqualTo(StatutReservation.CONFIRMEE);
        assertThatThrownBy(() -> workflow.confirmer(5L)).isInstanceOf(BusinessRuleException.class);
    }
}