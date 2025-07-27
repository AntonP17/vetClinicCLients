package by.antohakon.vetclinitclients.dto;

import by.antohakon.vetclinitclients.entity.AnimalType;

import java.util.UUID;

public record UpdateAnimalDto(AnimalType animalType, String animalName, UUID animalOwnerUuid) {
}
