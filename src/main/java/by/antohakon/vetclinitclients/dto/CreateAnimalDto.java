package by.antohakon.vetclinitclients.dto;

import by.antohakon.vetclinitclients.entity.AnimalType;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateAnimalDto(UUID animalOwnerUuid, AnimalType animalType, String animalName) {
}
