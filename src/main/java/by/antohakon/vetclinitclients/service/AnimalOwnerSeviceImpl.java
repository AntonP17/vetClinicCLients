package by.antohakon.vetclinitclients.service;

import by.antohakon.vetclinitclients.dto.AnimalOwnerDto;
import by.antohakon.vetclinitclients.dto.CreateAnimalOwnerDto;
import by.antohakon.vetclinitclients.entity.AnimalOwner;
import by.antohakon.vetclinitclients.repository.AnimalOwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnimalOwnerSeviceImpl implements AnimalOwnerService {

    private final AnimalOwnerRepository animalOwnerRepository;

    @Override
    public Page<AnimalOwnerDto> getAllAnimalOwners(Pageable pageable) {

        log.info("getAllAnimalOwners");
        return animalOwnerRepository.findAll(pageable)
                .map(owner -> AnimalOwnerDto.builder()
                        .animalOwnerUuid(owner.getAnimalOwnerUuid())
                        .firstName(owner.getFirstName())
                        .lastName(owner.getLastName())
                        .build());

    }

    @Override
    public AnimalOwnerDto getAnimalOwnerById(UUID id) {

        log.info("method getAnimalOwnerById try get owner by id: {}", id);
        AnimalOwner findAnimalOwner = animalOwnerRepository.findByAnimalOwnerUuid(id);
        if (findAnimalOwner == null) {
            throw new RuntimeException("AnimalOwner not found with id: " + id); // сделать кастомный эксепшн
        }

        AnimalOwnerDto owner = AnimalOwnerDto.builder() // можно заменить на мапстракт
                .animalOwnerUuid(findAnimalOwner.getAnimalOwnerUuid())
                .firstName(findAnimalOwner.getFirstName())
                .lastName(findAnimalOwner.getLastName())
                .build();

        log.info("method getAnimalOwnerById return animalOwner: {}", owner);
        return owner;

    }

    @Override
    public AnimalOwnerDto createAnimalOwner(CreateAnimalOwnerDto owner) {

        log.info("method createAnimalOwner");
        log.info("try find exist Owner by last name");
        if (animalOwnerRepository.existsAnimalOwnersByLastName(owner.lastName())) {
            throw new RuntimeException("Owner already exists with last name: " + owner.lastName()); // тоже кастом сделать
        }

        log.info("try save animalOwner to DB");
        AnimalOwner newOwner = AnimalOwner.builder()
                .animalOwnerUuid(UUID.randomUUID())
                .firstName(owner.firstName())
                .lastName(owner.lastName())
                .build();
        animalOwnerRepository.save(newOwner);

        log.info("successfully save animalOwner to DB: {}", newOwner);

        AnimalOwnerDto animalOwnerDto = AnimalOwnerDto.builder()
                .animalOwnerUuid(newOwner.getAnimalOwnerUuid())
                .firstName(newOwner.getFirstName())
                .lastName(newOwner.getLastName())
                .build();

        log.info("method createAnimalOwner return animalOwner: {}", animalOwnerDto);
        return animalOwnerDto;

    }

    @Override
    public AnimalOwnerDto updateAnimalOwner(CreateAnimalOwnerDto owner, UUID id) {

        log.info("method updateAnimalOwner");
        AnimalOwner findAnimalOwner = animalOwnerRepository.findByAnimalOwnerUuid(id);
        if (findAnimalOwner == null) {
            throw new RuntimeException("AnimalOwner not found with id: " + id); // сделать кастомный эксепшн
        }

        log.info("try update animalOwner to DB");
        findAnimalOwner.setFirstName(owner.firstName());
        findAnimalOwner.setLastName(owner.lastName());

        animalOwnerRepository.save(findAnimalOwner);
        log.info("successfully update animalOwner to DB: {}", findAnimalOwner);

        AnimalOwnerDto updateUser = AnimalOwnerDto.builder()
                .firstName(findAnimalOwner.getFirstName())
                .lastName(findAnimalOwner.getLastName())
                .build();

        log.info("method updateAnimalOwner return updateUser: {}", updateUser);
        return updateUser;

    }

    @Override
    public void deleteAnimalOwner(UUID id) {

        log.info("method deleteAnimalOwner");
        AnimalOwner findAnimalOwner = animalOwnerRepository.findByAnimalOwnerUuid(id);
        if (findAnimalOwner == null) {
            throw new RuntimeException("AnimalOwner not found with id: " + id); // сделать кастомный эксепшн
        }

        animalOwnerRepository.delete(findAnimalOwner);

    }
}
