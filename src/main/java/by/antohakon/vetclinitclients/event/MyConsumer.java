package by.antohakon.vetclinitclients.event;

import by.antohakon.vetclinitclients.dto.AnimalDto;
import by.antohakon.vetclinitclients.dto.VisitInfoDto;
import by.antohakon.vetclinitclients.entity.Animal;
import by.antohakon.vetclinitclients.exceptions.AnimalNotFoundException;
import by.antohakon.vetclinitclients.repository.AnimalRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyConsumer {

    private final AnimalRepository animalRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "animals_owners",
            groupId = "animalsOwnersGroup",
            containerFactory = "animalsOwnersKafkaListenerContainerFactory")
    public void listenAnimalOwners(String message) {

        VisitInfoDto visitInfoDto = null;
        try {
            log.info("Take message {}", message);
             visitInfoDto = objectMapper.readValue(message, VisitInfoDto.class);

            log.info("after parsing {}", visitInfoDto.toString());
        } catch (JsonProcessingException e) {
            log.error("Failed to parse order from JSON: {}", message, e);
        }

        log.info("try Get animal by id: {}", visitInfoDto);
        Animal findAnimal = animalRepository.findByAnimalId(visitInfoDto.animalId());
        if (findAnimal == null) {
            throw new AnimalNotFoundException("Animal not found with id: " + visitInfoDto.animalId());
        }

        AnimalDto animal = AnimalDto.builder()
                .id(findAnimal.getAnimalId())
                .animalType(findAnimal.getAnimalType())
                .animalName(findAnimal.getAnimalName())
                .build();

        log.info(" return animal: {}", animal);

    }

}
