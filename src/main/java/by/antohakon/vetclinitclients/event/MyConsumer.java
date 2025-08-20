package by.antohakon.vetclinitclients.event;

import by.antohakon.vetclinitclients.dto.AnimalAndOwnerEvent;
import by.antohakon.vetclinitclients.dto.AnimalDto;
import by.antohakon.vetclinitclients.dto.ExceptionNotFoundDto;
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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
            containerFactory = "animalsOwnersKafkaListenerContainerFactory"
    )
    public void listenAnimalOwners(String message) {
        try {

            VisitInfoDto visitInfoDto = parseMessage(message);
            processAnimalAndOwnerRequest(visitInfoDto);

        } catch (AnimalNotFoundException e) {
            handleAnimalNotFound(e, getVisitInfoDtoFromMessage(message));
        } catch (OwnerNotFoundException e) {
            handleAnimalOwnerNotFound(e, getVisitInfoDtoFromMessage(message));
        } catch (JsonProcessingException e) {
            handleParsingError(message, e);
        } catch (Exception e) {
            handleGenericError(e, message);
        }
    }

    private VisitInfoDto getVisitInfoDtoFromMessage(String message) {
        try {
            return objectMapper.readValue(message, VisitInfoDto.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse message for error handling: {}", message);
            return null;
        }
    }

    private VisitInfoDto parseMessage(String message) throws JsonProcessingException {
        log.info("Received message: {}", message);
        VisitInfoDto visitInfoDto = objectMapper.readValue(message, VisitInfoDto.class);
        log.info("Parsed successfully: {}", visitInfoDto);
        return visitInfoDto;
    }

    private void processAnimalAndOwnerRequest(VisitInfoDto visitInfoDto) throws JsonProcessingException {
        Animal animal = findAnimalById(visitInfoDto.animalId());
        AnimalOwner animalOwner = findAnimalOwnerById(visitInfoDto.ownerId());

        AnimalAndOwnerEvent animalAndOwnerEvent = createAnimalAndOwnerEvent(visitInfoDto, animal, animalOwner);
        sendSuccessResponse(animalAndOwnerEvent);
    }

    private Animal findAnimalById(UUID animalId) {
        log.info("Searching animal by ID: {}", animalId);
        Animal animal = animalRepository.findByAnimalId(animalId);
        if (animal == null) {
            throw new AnimalNotFoundException("Animal not found with id: " + animalId);
        }
        return animal;
    }

    private AnimalOwner findAnimalOwnerById(UUID ownerId) {
        log.info("Searching animal owner by ID: {}", ownerId);
        AnimalOwner animalOwner = animalOwnerRepository.findByAnimalOwnerUuid(ownerId);
        if (animalOwner == null) {
            throw new OwnerNotFoundException("Animal owner not found with id: " + ownerId);
        }
        return animalOwner;
    }

    private AnimalAndOwnerEvent createAnimalAndOwnerEvent(VisitInfoDto visitInfoDto, Animal animal, AnimalOwner animalOwner) {
        return AnimalAndOwnerEvent.builder()
                .visitId(visitInfoDto.visitId())
                .animalName(animal.getAnimalName())
                .fullName(animalOwner.getLastName() + " " + animalOwner.getFirstName())
                .build();
    }

    private void sendSuccessResponse(AnimalAndOwnerEvent event) throws JsonProcessingException {
        log.info("Sending response: Animal: {}, Owner: {}", event.animalName(), event.fullName());
        String json = objectMapper.writeValueAsString(event);
        kafkaTemplate.send("client_visit_response", json);
    }

    private void handleAnimalNotFound(AnimalNotFoundException e, VisitInfoDto visitInfoDto) {
        String errorMessage = "Animal not found with id: " + visitInfoDto.animalId();
        ExceptionNotFoundDto exceptionNotFoundDto = new ExceptionNotFoundDto(errorMessage, visitInfoDto.visitId());
        sendErrorResponse(exceptionNotFoundDto);
        log.error(errorMessage);
    }

    private void handleAnimalOwnerNotFound(OwnerNotFoundException e, VisitInfoDto visitInfoDto) {
        String errorMessage = "Animal owner not found with id: " + visitInfoDto.ownerId();
        ExceptionNotFoundDto exceptionNotFoundDto = new ExceptionNotFoundDto(errorMessage, visitInfoDto.visitId());
        sendErrorResponse(exceptionNotFoundDto);
        log.error(errorMessage);
    }

    private void sendErrorResponse(Object errorDto) {
        try {
            String errorResponse = objectMapper.writeValueAsString(errorDto);
            kafkaTemplate.send("exceptions", errorResponse);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize error response: {}", e.getMessage());
        }
    }

    private void handleParsingError(String message, JsonProcessingException e) {
        log.error("Failed to parse message from JSON: {}", message, e);
        ExceptionNotFoundDto errorDto = new ExceptionNotFoundDto("Invalid JSON format", null);
        sendErrorResponse(errorDto);
    }

    private void handleGenericError(Exception e, String message) {
        log.error("Unexpected error processing message: {}", message, e);
        ExceptionNotFoundDto errorDto = new ExceptionNotFoundDto("Internal server error", null);
        sendErrorResponse(errorDto);
    }

}
