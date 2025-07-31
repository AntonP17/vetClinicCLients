package by.antohakon.vetclinitclients.service;

import by.antohakon.vetclinitclients.dto.AnimalDto;
import by.antohakon.vetclinitclients.dto.CreateAnimalDto;
import by.antohakon.vetclinitclients.dto.UpdateAnimalDto;
import by.antohakon.vetclinitclients.entity.Animal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AnimalService {

    Page<AnimalDto> getAllAnimals(Pageable pageable);
    AnimalDto getAnimalById(UUID id);
    AnimalDto createAnimal(CreateAnimalDto animal);
    AnimalDto updateAnimal(UpdateAnimalDto animal, UUID id);
    void deleteAnimal(UUID id);

}
