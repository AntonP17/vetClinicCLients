package by.antohakon.vetclinitclients.service;

import by.antohakon.vetclinitclients.dto.AnimalDto;
import by.antohakon.vetclinitclients.dto.CreateAnimalDto;
import by.antohakon.vetclinitclients.dto.UpdateAnimalDto;
import by.antohakon.vetclinitclients.entity.Animal;
import by.antohakon.vetclinitclients.entity.AnimalOwner;
import by.antohakon.vetclinitclients.exceptions.AnimalNotFoundException;
import by.antohakon.vetclinitclients.exceptions.OwnerNotFoundException;
import by.antohakon.vetclinitclients.repository.AnimalOwnerRepository;
import by.antohakon.vetclinitclients.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnimalServiceImpl implements AnimalService {

    private final AnimalRepository animalRepository;
    private final AnimalOwnerRepository animalOwnerRepository;

    @Override
    public Page<AnimalDto> getAllAnimals(Pageable pageable) {

        log.info("Get all animals");
        return animalRepository.findAll(pageable)
                .map(animal -> AnimalDto.builder()
                        .id(animal.getAnimalId())
                        .animalType(animal.getAnimalType())
                        .animalName(animal.getAnimalName())
                        .build());
    }

    @Override
    @Cacheable(value = "animal_cache", key = "#animalId")
    public AnimalDto getAnimalById(UUID animalId) {

        log.info("method getnimalByID, try Get animal by id: {}", animalId);
        Animal findAnimal = animalRepository.findByAnimalId(animalId);
        if (findAnimal == null) {
            throw new AnimalNotFoundException("Animal not found with id: " + animalId);
        }

        AnimalDto animal = AnimalDto.builder()
                .id(findAnimal.getAnimalId())
                .animalType(findAnimal.getAnimalType())
                .animalName(findAnimal.getAnimalName())
                .build();

        log.info("method getnimalByID, return animal: {}", animal);
        return animal;

    }

    @Override
    @CachePut(value = "animal_cache", key = "#result.id")
    public AnimalDto createAnimal(CreateAnimalDto animal) {

        log.info("method createAnimal");
        log.info("try find Owner byUUID in DB : {}", animal.animalOwnerUuid().toString());
        AnimalOwner findOwner = animalOwnerRepository.findByAnimalOwnerUuid(animal.animalOwnerUuid());
        if (findOwner == null) {
            throw new OwnerNotFoundException("Owner not found with UUID: " + animal.animalOwnerUuid());
        }
        log.info("successfully find Owner in DB : {}", findOwner);

        log.info("try save Animal to DB ");
        Animal newAnimal = Animal.builder()
                .animalId(UUID.randomUUID())
                .animalType(animal.animalType())
                .animalName(animal.animalName())
                .animalOwner(findOwner)
                .build();
        animalRepository.save(newAnimal);
        log.info("successfully save Animal in DB");

        AnimalDto animalDto = AnimalDto.builder()
                .id(newAnimal.getAnimalId())
                .animalType(newAnimal.getAnimalType())
                .animalName(newAnimal.getAnimalName())
                .build();

        log.info("return ANimalDTO : {}", animalDto);
        return animalDto;
    }

    @Override
    @CachePut(value = "animal_cache", key = "#animalId")
    public AnimalDto updateAnimal(UpdateAnimalDto animal, UUID animalId) {

        log.info("method updateAnimal");
        log.info("try find Animal byUUID in DB: {}", animal.animalOwnerUuid().toString());
        Animal findAnimal = animalRepository.findByAnimalId(animalId);
        if (findAnimal == null) {
            throw new AnimalNotFoundException("Animal not found with id: " + animalId);
        }

        log.info("try find Owner byUUID in DB : {}", animal.animalOwnerUuid().toString());
        AnimalOwner findOwner = animalOwnerRepository.findByAnimalOwnerUuid(animal.animalOwnerUuid());
        if (findOwner == null) {
            throw new OwnerNotFoundException("Owner not found with UUID: " + animal.animalOwnerUuid());
        }
        log.info("successfully find Animal in DB");

        log.info("try update Animal in DB");
        findAnimal.setAnimalOwner(findOwner);
        findAnimal.setAnimalType(animal.animalType());
        findAnimal.setAnimalName(animal.animalName());

        animalRepository.save(findAnimal);
        log.info("successfully update Animal in DB ");

        AnimalDto animalDto = AnimalDto.builder()
                .id(findAnimal.getAnimalId())
                .animalType(findAnimal.getAnimalType())
                .animalName(findAnimal.getAnimalName())
                .build();

        log.info("return ANimalDTO : {}", animalDto);
        return animalDto;

    }

    @Override
    @CacheEvict(value = "animal_cache", key = "#animalId")
    public void deleteAnimal(UUID animalId) {

        log.info("method deleteAnimal, try Delete animal by id: {}", animalId);
        Animal findAnimal = animalRepository.findByAnimalId(animalId);
        if (findAnimal == null) {
            throw new AnimalNotFoundException("Animal not found with id: " + animalId);
        }
        animalRepository.delete(findAnimal);
        log.info("animal with id {} deleted", animalId);

    }
}
