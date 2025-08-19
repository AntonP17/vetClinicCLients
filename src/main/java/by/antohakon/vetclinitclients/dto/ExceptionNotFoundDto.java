package by.antohakon.vetclinitclients.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ExceptionNotFoundDto(String errorMessage, UUID visitId) {
}
