package by.antohakon.vetclinitclients.service;

import by.antohakon.vetclinitclients.dto.AnimalOwnerDto;
import by.antohakon.vetclinitclients.dto.AnimalOwnersWithAnimalsDto;
import by.antohakon.vetclinitclients.dto.CreateAnimalOwnerDto;
import by.antohakon.vetclinitclients.entity.AnimalOwner;
import by.antohakon.vetclinitclients.exceptions.OwnerDublicateException;
import by.antohakon.vetclinitclients.exceptions.OwnerNotFoundException;
import by.antohakon.vetclinitclients.repository.AnimalOwnerRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class AnimalOwnerServiceTest {

    @Mock
    private AnimalOwnerRepository animalOwnerRepository;

    @InjectMocks
    private AnimalOwnerServiceImpl animalOwnerService;

    private static final UUID TEST_UUID = UUID.fromString("2e8f5f7b-7dfe-4c60-a03a-c38a1446033b");
    private AutoCloseable mockStatic;

    @BeforeEach
    void setUp() {
        mockStatic = Mockito.mockStatic(UUID.class);
        Mockito.when(UUID.randomUUID()).thenReturn(TEST_UUID);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockStatic.close();
    }

    @SneakyThrows
    @DisplayName("возврат из бд позитив")
    @Test
    void getAnimalOwnerById_Positive() {

        AnimalOwner owner = AnimalOwner.builder()
                .animalOwnerUuid(TEST_UUID)
                .firstName("John")
                .lastName("Doe")
                .animal(Collections.emptyList())
                .build();

        when(animalOwnerRepository.findByAnimalOwnerUuid(TEST_UUID)).thenReturn(owner);

        AnimalOwnersWithAnimalsDto result = animalOwnerService.getAnimalOwnerById(TEST_UUID);

        assertNotNull(result);
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());
        assertTrue(result.animals().isEmpty());

    }

    @Test
    @SneakyThrows
    @DisplayName("негатив не нашел в бд")
    void getAnimalOwnerById_Negatuve() {
        when(animalOwnerRepository.findByAnimalOwnerUuid(TEST_UUID)).thenReturn(null);

        assertThrows(OwnerNotFoundException.class, () -> {
            animalOwnerService.getAnimalOwnerById(TEST_UUID);
        });
    }

    @Test
    @SneakyThrows
    @DisplayName("сохранения в бд позитив")
    void createAnimalOwner_Positive() {

        CreateAnimalOwnerDto request = new CreateAnimalOwnerDto("Имя", "Фамилия");
        when(animalOwnerRepository.existsAnimalOwnersByLastName("Фамилия")).thenReturn(false);

        AnimalOwner savedOwner = AnimalOwner.builder()
                .animalOwnerUuid(TEST_UUID)
                .firstName("Имя")
                .lastName("Фамилия")
                .build();

        when(animalOwnerRepository.save(any(AnimalOwner.class))).thenReturn(savedOwner);

        AnimalOwnerDto result = animalOwnerService.createAnimalOwner(request);

        assertNotNull(result);
        assertEquals(TEST_UUID, result.animalOwnerUuid());
        assertEquals("Имя", result.firstName());
        assertEquals("Фамилия", result.lastName());
    }

    @Test
    @SneakyThrows
    @DisplayName("дупликат в БД")
    void createAnimalOwner_Negative() {
        CreateAnimalOwnerDto request = new CreateAnimalOwnerDto("Имя", "Фамилия");
        when(animalOwnerRepository.existsAnimalOwnersByLastName("Фамилия")).thenReturn(true);

        assertThrows(OwnerDublicateException.class, () -> {
            animalOwnerService.createAnimalOwner(request);
        });
    }

    @Test
    @SneakyThrows
    @DisplayName("обновление позитив")
    void updateAnimalOwner_Positive() {

        CreateAnimalOwnerDto updateRequest = new CreateAnimalOwnerDto("Имя", "Фамилия");
        AnimalOwner existingOwner = AnimalOwner.builder()
                .animalOwnerUuid(TEST_UUID)
                .firstName("Имя")
                .lastName("Фамилия")
                .build();

        when(animalOwnerRepository.findByAnimalOwnerUuid(TEST_UUID)).thenReturn(existingOwner);

        AnimalOwnerDto result = animalOwnerService.updateAnimalOwner(updateRequest, TEST_UUID);

        assertNotNull(result);
        assertEquals("Имя", result.firstName());
        assertEquals("Фамилия", result.lastName());
    }

    @Test
    @SneakyThrows
    @DisplayName("обновление не найден в БД")
    void updateAnimalOwner_negative() {
        CreateAnimalOwnerDto updateRequest = new CreateAnimalOwnerDto("Имя", "Фамилия");
        when(animalOwnerRepository.findByAnimalOwnerUuid(TEST_UUID)).thenReturn(null);

        assertThrows(OwnerNotFoundException.class, () -> {
            animalOwnerService.updateAnimalOwner(updateRequest, TEST_UUID);
        });
    }

    @Test
    @SneakyThrows
    @DisplayName("даление позитив")
    void deleteAnimalOwner_positive() {
        AnimalOwner existingOwner = AnimalOwner.builder()
                .animalOwnerUuid(TEST_UUID)
                .firstName("Имя")
                .lastName("Фамилия")
                .build();

        when(animalOwnerRepository.findByAnimalOwnerUuid(TEST_UUID)).thenReturn(existingOwner);
        doNothing().when(animalOwnerRepository).delete(existingOwner);

        assertDoesNotThrow(() -> {
            animalOwnerService.deleteAnimalOwner(TEST_UUID);
        });

    }

    @Test
    @SneakyThrows
    @DisplayName("удаление , не найден в БД")
    void deleteAnimalOwner_Negative() {
        when(animalOwnerRepository.findByAnimalOwnerUuid(TEST_UUID)).thenReturn(null);

        assertThrows(OwnerNotFoundException.class, () -> {
            animalOwnerService.deleteAnimalOwner(TEST_UUID);
        });
    }


}
