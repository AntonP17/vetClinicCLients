package by.antohakon.vetclinitclients.event;

import by.antohakon.vetclinitclients.dto.AnimalAndOwnerEvent;
import by.antohakon.vetclinitclients.dto.AnimalDto;
import by.antohakon.vetclinitclients.dto.VisitInfoDto;
import by.antohakon.vetclinitclients.entity.Animal;
import by.antohakon.vetclinitclients.entity.AnimalOwner;
import by.antohakon.vetclinitclients.exceptions.AnimalNotFoundException;
import by.antohakon.vetclinitclients.exceptions.OwnerNotFoundException;
import by.antohakon.vetclinitclients.repository.AnimalOwnerRepository;
import by.antohakon.vetclinitclients.repository.AnimalRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyConsumer {

    private final AnimalRepository animalRepository;
    private final AnimalOwnerRepository animalOwnerRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

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

        log.info("try Get animal,owner by id: {},{}", visitInfoDto.animalId(), visitInfoDto.ownerId());

        Animal findAnimal = animalRepository.findByAnimalId(visitInfoDto.animalId());
        AnimalOwner findAnimalOwner = animalOwnerRepository.findByAnimalOwnerUuid(visitInfoDto.ownerId());

        if (findAnimal == null && findAnimalOwner == null) {

            String errorMessage = "Animal not found with id: " + visitInfoDto.animalId() +
                    " and owner not found with id: " + visitInfoDto.ownerId();
            kafkaTemplate.send("exceptions", errorMessage);
            log.error("Animal not found with id: " + visitInfoDto.animalId());
            log.error("Animal owner not found with id: " + visitInfoDto.ownerId());
        }

        AnimalAndOwnerEvent animalAndOwnerEvent = AnimalAndOwnerEvent.builder()
                .visitId(visitInfoDto.visitId())
                .animalName(findAnimal.getAnimalName())
                .fullName(findAnimalOwner.getLastName() + " " + findAnimalOwner.getFirstName())
                .build();


        log.info(" return animal and owner: {},{}", animalAndOwnerEvent.animalName(),animalAndOwnerEvent.fullName());
        try {
            String json = objectMapper.writeValueAsString(animalAndOwnerEvent);
            kafkaTemplate.send("client_visit_response", json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize order: {}", e.getMessage());
        }
    }
}
