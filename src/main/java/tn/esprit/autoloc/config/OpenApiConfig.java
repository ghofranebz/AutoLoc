package tn.esprit.autoloc.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bean de configuration : Spring appelle cette méthode au démarrage et range l'objet {@link OpenAPI}
 * dans le conteneur IoC. springdoc l'utilise pour l'en-tête de la documentation Swagger.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI autolocOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("AutoLoc API")
                .version("1.0.0")
                .description("API REST de gestion d'une agence de location de véhicules : "
                        + "agences, employés, clients, véhicules, équipements, réservations, contrats, "
                        + "paiements et maintenances."));
    }
}
