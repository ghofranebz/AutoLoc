package tn.esprit.autoloc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.dto.ClientRequest;
import tn.esprit.autoloc.dto.ClientResponse;

/** Mapper MapStruct : l'implémentation ClientMapperImpl est générée à la compilation (bean Spring). */
@Component
@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "idClient", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    Client toEntity(ClientRequest request);

    @Mapping(target = "idClient", ignore = true)
    @Mapping(target = "dateInscription", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    void apply(ClientRequest request, @MappingTarget Client client);

    ClientResponse toResponse(Client client);
}