package by.antohakon.vetclinitclients.dto;

import by.antohakon.vetclinitclients.entity.AnimalType;

import java.util.UUID;

public record CreateAnimalDto(UUID animalOwnerUuid, AnimalType animalType, String animalName) {
}
