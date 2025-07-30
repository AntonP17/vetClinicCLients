package by.antohakon.vetclinitclients.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
// сюда можно добавить еще поле списка всех животных владельца , сделать позже
public record AnimalOwnerDto(UUID animalOwnerUuid, String firstName, String lastName) {
}
