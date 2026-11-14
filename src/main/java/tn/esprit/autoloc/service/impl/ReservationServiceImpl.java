package tn.esprit.autoloc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.dto.PeriodeRequest;
import tn.esprit.autoloc.dto.ReservationRequest;
import tn.esprit.autoloc.dto.ReservationResponse;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.mapper.ReservationMapper;
import tn.esprit.autoloc.repository.ClientRepository;
import tn.esprit.autoloc.repository.ContratRepository;
import tn.esprit.autoloc.repository.ReservationRepository;
import tn.esprit.autoloc.repository.VehiculeRepository;
import tn.esprit.autoloc.service.IReservationService;
import tn.esprit.autoloc.service.support.EntityLoader;
import tn.esprit.autoloc.service.support.PeriodeValidator;
import tn.esprit.autoloc.service.validation.ReservationValidator;

import java.time.LocalDate;
import java.util.List;

/**
 * SRP : ce service orchestre « charger -> valider -> enregistrer ».
 * La validation est déléguée à {@link ReservationValidator}, les transitions de statut
 * à ReservationWorkflowServiceImpl.
 * DIP : toutes les dépendances sont des abstractions injectées par le constructeur.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ReservationServiceImpl implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final ClientRepository clientRepository;
    private final VehiculeRepository vehiculeRepository;
    private final ContratRepository contratRepository;
    private final ReservationMapper mapper;
    private final EntityLoader loader;
    private final PeriodeValidator periodeValidator;
    private final ReservationValidator reservationValidator;

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getAll() {
        return reservationRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse getById(Long id) {
        return mapper.toResponse(charger(id));
    }

    @Override
    public ReservationResponse creer(ReservationRequest request) {
        Client client = loader.charger(clientRepository, request.idClient(), "Client");
        Vehicule vehicule = loader.charger(vehiculeRepository, request.idVehicule(), "Vehicule");

        reservationValidator.validerCreation(vehicule, request.dateDebut(), request.dateFin());

        Reservation reservation = mapper.toEntity(request);
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        reservation.setClient(client);
        reservation.setVehicule(vehicule);
        return mapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    public ReservationResponse modifierDates(Long id, PeriodeRequest periode) {
        Reservation existante = charger(id);
        if (existante.getStatut() != StatutReservation.EN_ATTENTE) {
            throw new BusinessRuleException("Seule une réservation EN_ATTENTE peut être modifiée");
        }
        reservationValidator.validerNouvellesDates(id, existante.getVehicule(), periode.dateDebut(), periode.dateFin());
        existante.setDateDebut(periode.dateDebut());
        existante.setDateFin(periode.dateFin());
        return mapper.toResponse(reservationRepository.save(existante));
    }

    @Override
    public void delete(Long id) {
        Reservation reservation = charger(id);
        if (contratRepository.existsByReservation_IdReservation(id)) {
            throw new BusinessRuleException("Impossible de supprimer la réservation " + id + " : un contrat y est lié");
        }
        reservationRepository.delete(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> findByClient(Long idClient) {
        return reservationRepository.findByClient_IdClient(idClient).stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> findByVehicule(Long idVehicule) {
        return reservationRepository.findByVehicule_IdVehicule(idVehicule).stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> findByStatut(StatutReservation statut) {
        return reservationRepository.findByStatutOrderByDateDebutAsc(statut).stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> findEntre(LocalDate debut, LocalDate fin) {
        periodeValidator.verifier(debut, fin);
        return reservationRepository.findByDateDebutBetween(debut, fin).stream().map(mapper::toResponse).toList();
    }

    private Reservation charger(Long id) {
        return loader.charger(reservationRepository, id, "Reservation");
    }
}
