package by.antohakon.vetclinitclients.dto;

import lombok.Builder;

@Builder
public record AnimalAndOwnerEvent(String animalName, String fullName) {
}
