package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.dto.SituationPaiementResponse;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.ContratRepository;
import tn.esprit.autoloc.repository.PaiementRepository;
import tn.esprit.autoloc.service.impl.FacturationServiceImpl;
import tn.esprit.autoloc.service.support.EntityLoader;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/** DIP : le service est testé avec des repositories simulés, sans Spring ni base de données. */
@ExtendWith(MockitoExtension.class)
class FacturationServiceImplTest {

    @Mock ContratRepository contratRepository;
    @Mock PaiementRepository paiementRepository;

    private FacturationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new FacturationServiceImpl(contratRepository, paiementRepository, new EntityLoader());
    }

    @Test
    void situationPaiement_calcule_le_reste_a_payer() {
        Contrat contrat = new Contrat();
        contrat.setIdContrat(1L);
        contrat.setMontantTotal(new BigDecimal("360.00"));
        when(contratRepository.findById(1L)).thenReturn(Optional.of(contrat));
        when(paiementRepository.totalPayeParContrat(1L)).thenReturn(new BigDecimal("200.00"));

        SituationPaiementResponse s = service.situationPaiement(1L);

        assertThat(s.montantTotal()).isEqualByComparingTo("360.00");
        assertThat(s.totalPaye()).isEqualByComparingTo("200.00");
        assertThat(s.resteAPayer()).isEqualByComparingTo("160.00");
    }

    @Test
    void contrat_inconnu_renvoie_404() {
        when(contratRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.situationPaiement(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
