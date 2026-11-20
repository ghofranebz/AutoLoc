package tn.esprit.autoloc.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.dto.ContratResponse;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.exception.ConflictException;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.ContratMapper;
import tn.esprit.autoloc.repository.ContratRepository;
import tn.esprit.autoloc.repository.ReservationRepository;
import tn.esprit.autoloc.service.IContratService;
import tn.esprit.autoloc.service.ITarificationService;
import tn.esprit.autoloc.service.support.EntityLoader;
import tn.esprit.autoloc.service.support.Fuseau;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * SRP : cycle de vie du contrat uniquement. La facturation est dans FacturationServiceImpl,
 * le calcul du montant dans ITarificationService (patron Strategy, OCP).
 */
@Service
@Transactional
public class ContratServiceImpl implements IContratService {

    private final ContratRepository contratRepository;
    private final ReservationRepository reservationRepository;
    private final ContratMapper mapper;
    private final EntityLoader loader;
    private final ITarificationService tarificationService;
    private final String typeTarification;

    /** Constructeur écrit à la main car @Value sur un paramètre n'est pas recopié par Lombok. */
    public ContratServiceImpl(ContratRepository contratRepository,
                              ReservationRepository reservationRepository,
                              ContratMapper mapper,
                              EntityLoader loader,
                              ITarificationService tarificationService,
                              @Value("${autoloc.tarification:STANDARD}") String typeTarification) {
        this.contratRepository = contratRepository;
        this.reservationRepository = reservationRepository;
        this.mapper = mapper;
        this.loader = loader;
        this.tarificationService = tarificationService;
        this.typeTarification = typeTarification;
        if (!tarificationService.existe(typeTarification)) {
            throw new IllegalStateException("Type de tarification inconnu : '" + typeTarification
                    + "'. Valeurs possibles : " + tarificationService.typesDisponibles());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContratResponse> getAll() {
        return contratRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ContratResponse getById(Long id) {
        return mapper.toResponse(charger(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ContratResponse getByReservation(Long idReservation) {
        return contratRepository.findByReservation_IdReservation(idReservation)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun contrat pour la réservation " + idReservation));
    }

    @Override
    public ContratResponse creer(Long idReservation) {
        Reservation reservation = loader.charger(reservationRepository, idReservation, "Reservation");
        if (reservation.getStatut() != StatutReservation.CONFIRMEE) {
            throw new BusinessRuleException("La réservation doit être CONFIRMEE pour créer un contrat (statut actuel : "
                    + reservation.getStatut() + ")");
        }
        if (contratRepository.existsByReservation_IdReservation(idReservation)) {
            throw new ConflictException("Un contrat existe déjà pour la réservation " + idReservation);
        }

        int jours = (int) Math.max(1, ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin()));
        double prix = tarificationService.calculer(typeTarification,
                reservation.getVehicule().getTarifJournalier().doubleValue(), jours);
        BigDecimal montant = BigDecimal.valueOf(prix).setScale(2, RoundingMode.HALF_UP);

        Contrat contrat = new Contrat();
        contrat.setReservation(reservation);
        contrat.setDateSignature(LocalDate.now(Fuseau.TUNIS));        contrat.setMontantTotal(montant);
        contrat.setValide(true);

        reservation.getVehicule().setStatut(StatutVehicule.LOUE);
        return mapper.toResponse(contratRepository.save(contrat));
    }

    @Override
    public ContratResponse changerValidite(Long id, boolean valide) {
        Contrat contrat = charger(id);
        contrat.setValide(valide);
        return mapper.toResponse(contratRepository.save(contrat));
    }

    @Override
    public void delete(Long id) {
        // les paiements sont supprimés en cascade (CascadeType.ALL + orphanRemoval)
        contratRepository.delete(charger(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContratResponse> findByValide(boolean valide) {
        return contratRepository.findByValide(valide).stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContratResponse> findSigneEntre(LocalDate debut, LocalDate fin) {
        return contratRepository.findByDateSignatureBetween(debut, fin).stream().map(mapper::toResponse).toList();
    }

    private Contrat charger(Long id) {
        return loader.charger(contratRepository, id, "Contrat");
    }
}