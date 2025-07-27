package by.antohakon.vetclinitclients.dto;

import by.antohakon.vetclinitclients.entity.AnimalType;
import lombok.Builder;

import java.util.UUID;

@Builder
public record AnimalDto(UUID id, AnimalType animalType,String animalName) {
}
