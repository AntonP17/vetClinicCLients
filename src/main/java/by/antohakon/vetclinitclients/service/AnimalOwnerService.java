package by.antohakon.vetclinitclients.service;

import by.antohakon.vetclinitclients.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AnimalOwnerService {

    Page<AnimalOwnerDto> getAllAnimalOwners(Pageable pageable);
    AnimalOwnersWithAnimalsDto getAnimalOwnerById(UUID id);
    AnimalOwnerDto createAnimalOwner(CreateAnimalOwnerDto newOwner);
    AnimalOwnerDto updateAnimalOwner(CreateAnimalOwnerDto owner, UUID id);
    void deleteAnimalOwner(UUID id);


}
