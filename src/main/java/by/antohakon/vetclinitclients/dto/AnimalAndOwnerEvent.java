package by.antohakon.vetclinitclients.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AnimalAndOwnerEvent(UUID visitId, String animalName, String fullName) {
}
