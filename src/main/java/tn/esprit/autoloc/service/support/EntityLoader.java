package tn.esprit.autoloc.service.support;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.exception.ResourceNotFoundException;

/**
 * SRP / DRY : une seule responsabilité, charger une entité par id ou lever un 404.
 * Remplace les findById(...).orElseThrow(...) qui étaient recopiés dans chaque service.
 */
@Component
public class EntityLoader {

    public <T> T charger(JpaRepository<T, Long> repository, Long id, String ressource) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ressource + " introuvable avec l'id " + id));
    }
}
