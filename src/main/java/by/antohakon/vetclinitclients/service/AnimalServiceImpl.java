package by.antohakon.vetclinitclients.service;

import by.antohakon.vetclinitclients.dto.AnimalDto;
import by.antohakon.vetclinitclients.dto.CreateAnimalDto;
import by.antohakon.vetclinitclients.entity.Animal;
import by.antohakon.vetclinitclients.entity.AnimalOwner;
import by.antohakon.vetclinitclients.repository.AnimalOwnerRepository;
import by.antohakon.vetclinitclients.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public AnimalDto getAnimalById(UUID id) {

        log.info("method getnimalByID, try Get animal by id: {}", id);
        Animal findAnimal = animalRepository.findByAnimalId(id); // проверить на null лиюо оптионал лиюо ифы сделать
        if (findAnimal == null) {
            throw new RuntimeException("Animal not found with id: " + id);
            //log.info("exception in method getAnimalById");
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
    public AnimalDto createAnimal(CreateAnimalDto animal) {

        log.info("method createAnimal");
        log.info("try find Owner byUUID in DB ");
        AnimalOwner owner = animalOwnerRepository.findById(animal.animalOwnerUuid());
        Animal newAnimal = Animal.builder()
                .animalId(UUID.randomUUID())
                .animalType(animal.animalType())
                .animalName(animal.animalName())

                .animalOwner(
                        animal.animalOwnerUuid())

                .build();

    }

    @Override
    public AnimalDto updateAnimal(CreateAnimalDto animal, UUID id) {
        return null;
    }

    @Override
    public void deleteAnimal(UUID id) {

    }
}
