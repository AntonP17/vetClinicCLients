package by.antohakon.vetclinitclients.service;

import org.springframework.data.domain.Pageable;

public interface AnimalOwnerService {

    Page<AnimalOwnerDto> getAllAnimalOwners(Pageable pageable);


}
