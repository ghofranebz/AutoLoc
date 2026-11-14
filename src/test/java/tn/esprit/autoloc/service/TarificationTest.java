package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.repository.VehiculeRepository;
import tn.esprit.autoloc.service.impl.TarificationService;
import tn.esprit.autoloc.service.support.EntityLoader;
import tn.esprit.autoloc.service.tarification.StrategieTarification;
import tn.esprit.autoloc.service.tarification.TarificationEntreprise;
import tn.esprit.autoloc.service.tarification.TarificationLongueDuree;
import tn.esprit.autoloc.service.tarification.TarificationStandard;
import tn.esprit.autoloc.service.tarification.TarificationWeekend;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.mock;

/** Atelier GoF/SOLID, partie C : patron Strategy et principe OCP. */
class TarificationTest {

    private Map<String, StrategieTarification> strategies;
    private TarificationService service;

    @BeforeEach
    void setUp() {
        strategies = new HashMap<>(Map.of(
                "STANDARD", new TarificationStandard(),
                "WEEKEND", new TarificationWeekend(),
                "LONGUE_DUREE", new TarificationLongueDuree()));
        service = new TarificationService(strategies, mock(VehiculeRepository.class), new EntityLoader());
    }

    @Test
    void chaque_strategie_ne_contient_que_sa_formule() {
        assertThat(new TarificationStandard().calculer(100, 5)).isEqualTo(500.0);
        assertThat(new TarificationWeekend().calculer(100, 5)).isCloseTo(450.0, within(0.001));
        assertThat(new TarificationLongueDuree().calculer(100, 5)).isCloseTo(400.0, within(0.001));
        assertThat(new TarificationEntreprise().calculer(100, 5)).isCloseTo(425.0, within(0.001));
    }

    @Test
    void le_service_choisit_la_strategie_demandee() {
        assertThat(service.calculer("STANDARD", 90, 4)).isEqualTo(360.0);
        assertThat(service.calculer("WEEKEND", 90, 4)).isEqualTo(324.0);
        assertThat(service.calculer("LONGUE_DUREE", 90, 4)).isEqualTo(288.0);
        assertThat(service.calculer("longue-duree", 90, 4)).isEqualTo(288.0);
    }

    @Test
    void ocp_ajouter_la_strategie_entreprise_sans_modifier_le_service() {
        assertThat(service.existe("ENTREPRISE")).isFalse();
        strategies.put("ENTREPRISE", new TarificationEntreprise());
        assertThat(service.calculer("ENTREPRISE", 100, 10)).isEqualTo(850.0);
        assertThat(service.typesDisponibles()).containsExactly("ENTREPRISE", "LONGUE_DUREE", "STANDARD", "WEEKEND");
    }

    @Test
    void type_inconnu_est_refuse_avec_la_liste_des_types() {
        assertThatThrownBy(() -> service.calculer("ETUDIANT", 90, 4))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("[LONGUE_DUREE, STANDARD, WEEKEND]");
    }

    @Test
    void nombre_de_jours_et_tarif_doivent_etre_positifs() {
        assertThatThrownBy(() -> service.calculer("STANDARD", 90, 0)).isInstanceOf(BusinessRuleException.class);
        assertThatThrownBy(() -> service.calculer("STANDARD", 0, 3)).isInstanceOf(BusinessRuleException.class);
    }
}