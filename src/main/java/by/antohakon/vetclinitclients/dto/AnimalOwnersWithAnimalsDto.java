package by.antohakon.vetclinitclients.dto;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record AnimalOwnersWithAnimalsDto(UUID animalOwnerUuid, String firstName, String lastName, List<AnimalDto> animals) {
}
