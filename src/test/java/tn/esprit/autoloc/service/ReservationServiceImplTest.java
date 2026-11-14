package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.ReservationRequest;
import tn.esprit.autoloc.dto.ReservationResponse;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.exception.ConflictException;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.ReservationMapper;
import tn.esprit.autoloc.repository.ClientRepository;
import tn.esprit.autoloc.repository.ContratRepository;
import tn.esprit.autoloc.repository.ReservationRepository;
import tn.esprit.autoloc.repository.VehiculeRepository;
import tn.esprit.autoloc.service.impl.ReservationServiceImpl;
import tn.esprit.autoloc.service.support.EntityLoader;
import tn.esprit.autoloc.service.support.PeriodeValidator;
import tn.esprit.autoloc.service.validation.ReservationValidator;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * DIP/DI : le service ne dépend que d'abstractions (interfaces de repository), donc on le teste
 * sans Spring ni base de données en lui fournissant des mocks par le constructeur.
 */
@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock ReservationRepository reservationRepository;
    @Mock ClientRepository clientRepository;
    @Mock VehiculeRepository vehiculeRepository;
    @Mock ContratRepository contratRepository;

    ReservationServiceImpl service;

    private final LocalDate debut = LocalDate.of(2026, 11, 1);
    private final LocalDate fin = LocalDate.of(2026, 11, 5);

    @BeforeEach
    void setUp() {
        service = new ReservationServiceImpl(reservationRepository, clientRepository, vehiculeRepository,
                contratRepository, new ReservationMapper(), new EntityLoader(), new PeriodeValidator(),
                new ReservationValidator(reservationRepository, new PeriodeValidator()));
    }

    private Vehicule vehicule(StatutVehicule statut) {
        Vehicule v = new Vehicule();
        v.setIdVehicule(2L);
        v.setStatut(statut);
        return v;
    }

    private Client client() {
        Client c = new Client();
        c.setIdClient(1L);
        return c;
    }

    @Test
    void creer_cree_une_reservation_EN_ATTENTE() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client()));
        when(vehiculeRepository.findById(2L)).thenReturn(Optional.of(vehicule(StatutVehicule.DISPONIBLE)));
        when(reservationRepository.existsChevauchement(2L, debut, fin)).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        ReservationResponse r = service.creer(new ReservationRequest(1L, 2L, debut, fin));

        assertThat(r.statut()).isEqualTo(StatutReservation.EN_ATTENTE);
        assertThat(r.idClient()).isEqualTo(1L);
        assertThat(r.idVehicule()).isEqualTo(2L);
    }

    @Test
    void creer_refuse_un_chevauchement_avec_409() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client()));
        when(vehiculeRepository.findById(2L)).thenReturn(Optional.of(vehicule(StatutVehicule.DISPONIBLE)));
        when(reservationRepository.existsChevauchement(2L, debut, fin)).thenReturn(true);

        assertThatThrownBy(() -> service.creer(new ReservationRequest(1L, 2L, debut, fin)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void creer_refuse_un_vehicule_en_maintenance() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client()));
        when(vehiculeRepository.findById(2L)).thenReturn(Optional.of(vehicule(StatutVehicule.MAINTENANCE)));

        assertThatThrownBy(() -> service.creer(new ReservationRequest(1L, 2L, debut, fin)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("maintenance");
    }

    @Test
    void creer_refuse_une_date_de_fin_avant_le_debut() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client()));
        when(vehiculeRepository.findById(2L)).thenReturn(Optional.of(vehicule(StatutVehicule.DISPONIBLE)));

        assertThatThrownBy(() -> service.creer(new ReservationRequest(1L, 2L, fin, debut)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void creer_leve_404_si_le_client_est_inconnu() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.creer(new ReservationRequest(1L, 2L, debut, fin)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Client introuvable avec l'id 1");
    }
}
